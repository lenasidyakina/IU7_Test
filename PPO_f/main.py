import unittest
import subprocess
import os
import time
import io
import psycopg2

class TestE2E(unittest.TestCase):
    JAR_PATH = r"./app_cli/build/libs/app_cli-1.0-SNAPSHOT.jar"

    def setUp(self):
        # Получаем данные PostgreSQL из окружения
        host = os.environ.get("POSTGRES_HOST", "localhost")
        port = int(os.environ.get("POSTGRES_PORT", 5432))
        user = os.environ.get("POSTGRES_USER", "testuser")
        password = os.environ.get("POSTGRES_PASSWORD", "testpassword")
        dbname = os.environ.get("POSTGRES_DB", "testdb")

        # Подключаемся к базе с повторными попытками
        connected = False
        for _ in range(20):
            try:
                conn = psycopg2.connect(
                    host=host,
                    port=port,
                    user=user,
                    password=password,
                    database=dbname
                )
                connected = True
                break
            except Exception:
                print("⏳ Ждём, пока PostgreSQL станет доступен...")
                time.sleep(3)

        if not connected:
            raise Exception("❌ Не удалось подключиться к PostgreSQL")

        cur = conn.cursor()

        # Создаём минимальные таблицы
        cur.execute("""
        CREATE TABLE IF NOT EXISTS tag (
            id   BIGSERIAL PRIMARY KEY,
            name TEXT UNIQUE
        );

        CREATE TABLE IF NOT EXISTS question (
            id BIGSERIAL PRIMARY KEY,
            is_extended BOOLEAN NOT NULL,
            question TEXT
        );

        CREATE TABLE IF NOT EXISTS question_tags (
            question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
            tags_id     BIGINT NOT NULL REFERENCES tag(id) ON DELETE CASCADE,
            UNIQUE (question_id, tags_id)
        );
        """)
        conn.commit()
        cur.close()
        conn.close()

        # Переменные окружения для JAR
        jdbc_url = f"jdbc:postgresql://{host}:{port}/{dbname}"
        self.env = os.environ.copy()
        self.env["SPRING_DATASOURCE_URL"] = jdbc_url
        self.env["SPRING_DATASOURCE_USERNAME"] = user
        self.env["SPRING_DATASOURCE_PASSWORD"] = password

        # Запуск JAR
        self.process = subprocess.Popen(
            ["java", "-Dserver.port=9196", "-Dfile.encoding=UTF-8", "-jar", self.JAR_PATH],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            env=self.env,
            bufsize=1
        )

        self.process.stdout = io.TextIOWrapper(self.process.stdout, encoding='utf-8', errors='replace')
        self.process.stdin = io.TextIOWrapper(self.process.stdin, encoding='utf-8', write_through=True)

    def tearDown(self):
        try:
            if self.process and self.process.poll() is None:
                self.process.terminate()
                try:
                    self.process.wait(timeout=5)
                except subprocess.TimeoutExpired:
                    self.process.kill()

            if hasattr(self.process, "stdout") and not self.process.stdout.closed:
                self.process.stdout.close()
            if hasattr(self.process, "stdin") and not self.process.stdin.closed:
                self.process.stdin.close()
        except Exception as e:
            print(f"⚠️ Ошибка при завершении JAR: {e}")

    def _write(self, text):
        self.process.stdin.write(text + "\n")
        self.process.stdin.flush()

    def _wait_for(self, substr, timeout=25):
        start = time.time()
        output = ""
        while time.time() - start < timeout:
            line = self.process.stdout.readline()
            if not line:
                time.sleep(0.1)
                continue
            print(line.strip())
            output += line
            if substr in line:
                return output
        raise AssertionError(f"Не дождались: '{substr}'\nВывод:\n{output}")

    def test_full_flow(self):
        # 1. Регистрация user1
        self._wait_for("1 - зарегистрироваться")
        self._write("1")
        self._wait_for("логин:")
        self._write("user1")
        self._wait_for("пароль:")
        self._write("pass1")

        # 2. Вход user1
        self._wait_for("2 - войти")
        self._write("2")
        self._wait_for("логин:")
        self._write("user1")
        self._wait_for("пароль:")
        self._write("pass1")

        # 3. Создание анкеты user1
        self._wait_for("1 - создать анкету")
        self._write("1")
        self._wait_for("Часть 1. Ответь на вопросы от своего лица.")
        self._write("1")
        self._wait_for("Введите вес этого вопроса")
        self._write("5")
        self._wait_for("How do you like to spend your time?")
        self._write("I love walking.")
        self._wait_for("Введите вес этого вопроса")
        self._write("5")
        self._wait_for("Часть 2. Ответь на вопросы от лица потенциального друга.")
        self._write("2")
        self._wait_for("Введите вес этого вопроса")
        self._write("5")
        self._wait_for("How do you like to spend your time?")
        self._write("I love walking.")
        self._wait_for("Введите вес этого вопроса")
        self._write("5")
        self._wait_for("Анкета успешно создана")

        # 4. Выход user1
        self._wait_for("1 - создать анкету")
        self._write("2")  # выйти

        # 5. Регистрация user2
        self._wait_for("1 - зарегистрироваться")
        self._write("1")
        self._wait_for("логин:")
        self._write("user2")
        self._wait_for("пароль:")
        self._write("pass2")

        # 6. Вход user2
        self._wait_for("2 - войти")
        self._write("2")
        self._wait_for("логин:")
        self._write("user2")
        self._wait_for("пароль:")
        self._write("pass2")

        # 7. Создание анкеты user2
        self._wait_for("1 - создать анкету")
        self._write("1")
        self._wait_for("Часть 1. Ответь на вопросы от своего лица.")
        self._write("1")
        self._wait_for("Введите вес этого вопроса")
        self._write("5")
        self._wait_for("How do you like to spend your time?")
        self._write("I love swimming.")
        self._wait_for("Введите вес этого вопроса")
        self._write("5")
        self._wait_for("Часть 2. Ответь на вопросы от лица потенциального друга.")
        self._write("2")
        self._wait_for("Введите вес этого вопроса")
        self._write("5")
        self._wait_for("How do you like to spend your time?")
        self._write("I love swimming.")
        self._wait_for("Введите вес этого вопроса")
        self._write("5")
        self._wait_for("Анкета успешно создана")

        # 8. Получение списка потенциальных друзей (user2)
        self._wait_for("1 - создать анкету")
        self._write("3")  # пункт "получить список потенциальных друзей"
        self._wait_for("Ваши потенциальные друзья:")

        # 9. Добавляем найденную анкету в избранное
        self._wait_for("1 - добавить в чёрный список")
        self._write("2")  # добавить в избранное
        self._wait_for("Выберете номер анкеты:")
        self._write("1")  # допустим, первая анкета

        # 10. Проверяем, что избранное отобразилось корректно
        self._wait_for("1 - создать анкету")
        self._write("4")  # вывести избранное
        self._wait_for("user1")  # ждём, что появится имя user1

        print("✅ E2E тест завершён успешно — анкета добавлена в избранное")


if __name__ == "__main__":
    unittest.main()
