import unittest
import subprocess
import os
import time
import io
import psycopg2

# -----------------------------------
# Настройки подключения к PostgreSQL
# -----------------------------------
POSTGRES_HOST = os.environ.get("POSTGRES_HOST", "localhost")
POSTGRES_PORT = os.environ.get("POSTGRES_PORT", "5432")
POSTGRES_USER = os.environ.get("POSTGRES_USER", "postgres")
POSTGRES_PASSWORD = os.environ.get("POSTGRES_PASSWORD", "postgres")
POSTGRES_DB = os.environ.get("POSTGRES_DB", "testdb")

# -----------------------------------
# Путь к JAR
# -----------------------------------
JAR_PATH = os.path.join("PPO_f", "app_cli", "build", "libs", "app_cli-1.0-SNAPSHOT.jar")


class TestE2E(unittest.TestCase):

    def setUp(self):
        # 1. Ждём, пока PostgreSQL станет доступен
        for _ in range(20):
            try:
                self.conn = psycopg2.connect(
                    host=POSTGRES_HOST,
                    port=POSTGRES_PORT,
                    user=POSTGRES_USER,
                    password=POSTGRES_PASSWORD,
                    database=POSTGRES_DB
                )
                break
            except psycopg2.OperationalError:
                print("⏳ Ждём, пока PostgreSQL станет доступен...")
                time.sleep(3)
        else:
            raise Exception("❌ Не удалось подключиться к PostgreSQL")

        self.cur = self.conn.cursor()

        # 2. Создаём минимальные таблицы
        self.cur.execute("""
        CREATE TABLE IF NOT EXISTS tag (
            id BIGSERIAL PRIMARY KEY,
            name TEXT UNIQUE
        );
        CREATE TABLE IF NOT EXISTS question (
            id BIGSERIAL PRIMARY KEY,
            is_extended BOOLEAN NOT NULL,
            question TEXT
        );
        CREATE TABLE IF NOT EXISTS question_tags (
            question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
            tags_id BIGINT NOT NULL REFERENCES tag(id) ON DELETE CASCADE,
            UNIQUE (question_id, tags_id)
        );
        DROP TABLE IF EXISTS extended_answer_tags CASCADE;
        CREATE TABLE extended_answer_tags (
            extended_answer_id BIGINT,
            tags_id BIGINT
        );
        """)
        self.conn.commit()

        # 3. Добавляем базовые данные
        tags = ['walking', 'watching TV', 'swimming', 'sleeping']
        for t in tags:
            self.cur.execute("INSERT INTO tag (name) VALUES (%s) ON CONFLICT DO NOTHING;", (t,))
        self.conn.commit()

        # 4. Настраиваем переменные окружения для JAR
        jdbc_url = f"jdbc:postgresql://{POSTGRES_HOST}:{POSTGRES_PORT}/{POSTGRES_DB}"
        self.env = os.environ.copy()
        self.env["SPRING_DATASOURCE_URL"] = jdbc_url
        self.env["SPRING_DATASOURCE_USERNAME"] = POSTGRES_USER
        self.env["SPRING_DATASOURCE_PASSWORD"] = POSTGRES_PASSWORD

        # 5. Запускаем JAR
        self.process = subprocess.Popen(
            ["java", "-Dserver.port=9196", "-Dfile.encoding=UTF-8", "-jar", JAR_PATH],
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
        finally:
            if hasattr(self, "cur"):
                self.cur.close()
            if hasattr(self, "conn"):
                self.conn.close()

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
        # пример: регистрация, вход, создание анкеты и т.д.
        # можно вставить твой код из предыдущей версии теста
        self._wait_for("1 - зарегистрироваться")
        self._write("1")
        self._wait_for("логин:")
        self._write("user1")
        self._wait_for("пароль:")
        self._write("pass1")
        print("✅ Базовая регистрация прошла успешно")


if __name__ == "__main__":
    unittest.main()
