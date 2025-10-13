import unittest
import subprocess
import os
import time
import io
from urllib.parse import urlparse
from testcontainers.postgres import PostgresContainer
import psycopg2

os.environ["TESTCONTAINERS_RYUK_DISABLED"] = "true"


class TestE2E(unittest.TestCase):
    JAR_PATH = os.path.join("PPO_f", "app_cli", "build", "libs", "app_cli-1.0-SNAPSHOT.jar")

    def setUp(self):
            # 1. Поднимаем PostgreSQL контейнер
            self.postgres = PostgresContainer("postgres:15-alpine")
            self.postgres.start()

            # 2. Подключаемся к БД
            conn = psycopg2.connect(
                host=self.postgres.get_container_host_ip(),
                port=self.postgres.get_exposed_port(5432),
                user=self.postgres.username,
                password=self.postgres.password,
                database=self.postgres.dbname
            )
            cur = conn.cursor()

            # 3. Создаём минимальные таблицы, которые Hibernate ожидает при старте
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

            # 4. Добавляем базовые теги
            tags = ['walking', 'watching TV', 'swimming', 'sleeping']
            for t in tags:
                cur.execute("INSERT INTO tag (name) VALUES (%s) ON CONFLICT (name) DO NOTHING;", (t,))

            # 5. Получаем id тегов
            cur.execute("SELECT id, name FROM tag;")
            tag_map = {name: id for id, name in cur.fetchall()}

            # 6. Добавляем вопросы (как в Main.basic)
            cur.execute("""
            INSERT INTO question (question, is_extended)
            VALUES
                ('Do you love swimming or watching TV?', FALSE),
                ('How do you like to spend your time?', TRUE)
            ON CONFLICT DO NOTHING;
            """)

            cur.execute("SELECT id, question FROM question;")
            q_map = {text: id for id, text in cur.fetchall()}

            # 7. Привязываем теги к вопросам
            cur.execute("""
            INSERT INTO question_tags (question_id, tags_id)
            VALUES
                (%s, %s),
                (%s, %s),
                (%s, %s),
                (%s, %s)
            ON CONFLICT DO NOTHING;
            """, (
                q_map['Do you love swimming or watching TV?'], tag_map['watching TV'],
                q_map['Do you love swimming or watching TV?'], tag_map['swimming'],
                q_map['How do you like to spend your time?'], tag_map['walking'],
                q_map['How do you like to spend your time?'], tag_map['sleeping'],
            ))

            cur.execute("""
            DROP TABLE IF EXISTS extended_answer_tags CASCADE;

            -- Создаём пустую таблицу без внешних ключей, просто чтобы Hibernate не ругался
            CREATE TABLE extended_answer_tags (
                extended_answer_id BIGINT,
                tags_id BIGINT
            );
            """)



            conn.commit()
            cur.close()
            conn.close()

            # 8. Настраиваем переменные окружения для jar
            parsed = urlparse(self.postgres.get_connection_url())
            jdbc_url = f"jdbc:postgresql://{parsed.hostname}:{parsed.port}{parsed.path}"

            self.env = os.environ.copy()
            self.env["SPRING_DATASOURCE_URL"] = jdbc_url
            self.env["SPRING_DATASOURCE_USERNAME"] = parsed.username
            self.env["SPRING_DATASOURCE_PASSWORD"] = parsed.password

            # 9. Запускаем jar
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
                # Пытаемся корректно завершить процесс
                self.process.terminate()
                try:
                    self.process.wait(timeout=5)
                except subprocess.TimeoutExpired:
                    self.process.kill()

            # Закрываем потоки, если они ещё открыты
            if hasattr(self.process, "stdout") and not self.process.stdout.closed:
                self.process.stdout.close()
            if hasattr(self.process, "stdin") and not self.process.stdin.closed:
                self.process.stdin.close()

        finally:
            if hasattr(self, "postgres"):
                self.postgres.stop()


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
        # программа напечатает user1 и id
        # теперь меню выбора
        self._wait_for("1 - добавить в чёрный список")
        self._write("2")  # добавить в избранное
        self._wait_for("Выберете номер анкеты:")
        self._write("1")  # допустим, первая анкета

        # 10. Проверяем, что избранное отобразилось корректно
        self._wait_for("1 - создать анкету")
        self._write("4")  # вывести избранное
        output = self._wait_for("user1")  # ждём, что появится имя user1

        print("✅ E2E тест завершён успешно — анкета добавлена в избранное")


if __name__ == "__main__":
    unittest.main()
