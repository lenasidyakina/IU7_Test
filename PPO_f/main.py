import unittest
import os
import psycopg2
import time
import requests

class TestE2E_API(unittest.TestCase):
    BASE_URL = "http://localhost:9099/api/v1"  # эндпоинт твоего app_cli_server
    JAR_PATH = "./PPO_f/app_cli_server/build/libs/app_cli_server-1.0-SNAPSHOT.jar"

    def setUp(self):
        # Параметры PostgreSQL
        self.db_host = os.getenv("POSTGRES_HOST", "postgres")
        self.db_port = os.getenv("POSTGRES_PORT", "5432")
        self.db_user = os.getenv("POSTGRES_USER", "testuser")
        self.db_pass = os.getenv("POSTGRES_PASSWORD", "testpassword")
        self.db_name = os.getenv("POSTGRES_DB", "testdb")

        # Ждём PostgreSQL
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
                print("⏳ Ждём PostgreSQL...")
                time.sleep(3)
        else:
            raise Exception("❌ Не удалось подключиться к PostgreSQL")

        # Создаём таблицы и тестовые данные (как раньше)
        cur = self.conn.cursor()
        cur.execute("""
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

        # Ждём, пока API поднимется
        for i in range(20):
            try:
                r = requests.get(f"{self.BASE_URL}/users", timeout=2)
                if r.status_code in (200, 404):
                    print("✅ API доступен")
                    break
            except Exception:
                print("⏳ Ждём API...")
                time.sleep(3)
        else:
            raise Exception("❌ API не ответил")

    def tearDown(self):
        if hasattr(self, "conn"):
            self.conn.close()

    # --- 🔹 Пример: регистрация пользователя через API ---
    def test_register_user(self):
        user_data = {
            "username": "user_api",
            "password": "pass_api",
            "age": 21,
            "gender": True
        }

        response = requests.post(f"{self.BASE_URL}/register", json=user_data)
        self.assertEqual(response.status_code, 200, msg=f"Ошибка регистрации: {response.text}")

        data = response.json()
        self.assertIn("id", data)
        self.assertEqual(data["name"], "user_api")
        print(f"✅ Пользователь зарегистрирован: {data}")

if __name__ == "__main__":
    unittest.main()
