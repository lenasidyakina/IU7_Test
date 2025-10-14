import unittest
import subprocess
import os
import time
import io
import psycopg2


class TestE2E(unittest.TestCase):
    JAR_PATH = "./PPO_f/app_cli/build/libs/app_cli-1.0-SNAPSHOT.jar"

    def setUp(self):
        # Параметры для CI (аналог integration-tests)
        self.db_host = os.getenv("POSTGRES_HOST", "localhost")
        self.db_port = os.getenv("POSTGRES_PORT", "5432")
        self.db_user = os.getenv("POSTGRES_USER", "testuser")
        self.db_pass = os.getenv("POSTGRES_PASSWORD", "testpassword")
        self.db_name = os.getenv("POSTGRES_DB", "testdb")
    
        # Ждём доступности PostgreSQL
        for _ in range(20):
            try:
                self.conn = psycopg2.connect(
                    host=self.db_host,
                    port=self.db_port,
                    user=self.db_user,
                    password=self.db_pass,
                    database=self.db_name
                )
                print("✅ PostgreSQL доступен")
                break
            except Exception:
                print("⏳ Ждём, пока PostgreSQL станет доступен...")
                time.sleep(3)
        else:
            raise Exception("❌ Не удалось подключиться к PostgreSQL")
    
        cur = self.conn.cursor()
    
        # Создаём таблицы
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
        DROP TABLE IF EXISTS extended_answer_tags CASCADE;
        CREATE TABLE extended_answer_tags (
            extended_answer_id BIGINT,
            tags_id BIGINT
        );
        """)
    
        tags = ['walking', 'watching TV', 'swimming', 'sleeping']
        for t in tags:
            cur.execute("INSERT INTO tag (name) VALUES (%s) ON CONFLICT (name) DO NOTHING;", (t,))
    
        cur.execute("""
        INSERT INTO question (question, is_extended)
        VALUES
            ('Do you love swimming or watching TV?', FALSE),
            ('How do you like to spend your time?', TRUE)
        ON CONFLICT DO NOTHING;
        """)
    
        cur.execute("""
        INSERT INTO question_tags (question_id, tags_id)
        SELECT q.id, t.id
        FROM question q, tag t
        WHERE (q.question, t.name) IN (
            ('Do you love swimming or watching TV?', 'watching TV'),
            ('Do you love swimming or watching TV?', 'swimming'),
            ('How do you like to spend your time?', 'walking'),
            ('How do you like to spend your time?', 'sleeping')
        )
        ON CONFLICT DO NOTHING;
        """)
    
        self.conn.commit()
        cur.close()
    
        # Переменные окружения для JAR
        jdbc_url = f"jdbc:postgresql://{self.db_host}:{self.db_port}/{self.db_name}"
        self.env = os.environ.copy()
        self.env["SPRING_DATASOURCE_URL"] = jdbc_url
        self.env["SPRING_DATASOURCE_USERNAME"] = self.db_user
        self.env["SPRING_DATASOURCE_PASSWORD"] = self.db_pass
    
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
        finally:
            if hasattr(self, "conn"):
                self.conn.close()

    def _write(self, text):
        self.process.stdin.write(text + "\n")
        self.process.stdin.flush()

    def _wait_for(self, substr, timeout=30):
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
        self._wait_for("1 - зарегистрироваться")
        self._write("1")
        self._wait_for("логин:")
        self._write("user1")
        self._wait_for("пароль:")
        self._write("pass1")
        print("✅ Регистрация прошла успешно")


if __name__ == "__main__":
    unittest.main()
