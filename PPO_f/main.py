import unittest
import os
import psycopg2
import time
import requests

class TestE2E_API(unittest.TestCase):
    BASE_URL = "http://localhost:9099/api/v1"

    def setUp(self):
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

        # Ждём API
        for _ in range(20):
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

    # --- 🔹 Полный E2E сценарий ---
    def test_two_users_quest_fav(self):
        # 1️⃣ Регистрация первого пользователя
        user1 = {"username": "alice", "password": "pass1", "age": 25, "gender": True}
        resp1 = requests.post(f"{self.BASE_URL}/register", json=user1)
        self.assertEqual(resp1.status_code, 200)
        user1_data = resp1.json()
        print("✅ User1 зарегистрирован:", user1_data)

        # 2️⃣ Логин первого пользователя
        login1 = {"name": "alice", "password": "pass1"}
        resp_login1 = requests.post(f"{self.BASE_URL}/login", json=login1)
        self.assertEqual(resp_login1.status_code, 200)
        token1 = resp_login1.json()["token"]

        # 3️⃣ Создание анкеты первого пользователя
        questions_resp = requests.get(f"{self.BASE_URL}/questions").json()
        print(questions_resp)
        quest_data1 = []
        for q in questions_resp:
            if q["is_extended"]:
                quest_data1.append({"type": "EXT", "answer1": "answerA1", "weight1": 1,
                                    "answer2": "answerA2", "weight2": 1})
            else:
                quest_data1.append({"type": "VAR", "answer1": "swimming", "weight1": 1,
                                    "answer2": "swimming", "weight2": 1})

        headers1 = {"Authorization": f"Bearer {token1}"}
        resp_quest1 = requests.post(f"{self.BASE_URL}/quest", json=quest_data1, headers=headers1)
        self.assertEqual(resp_quest1.status_code, 201)
        print("✅ User1 создал анкету")

        # 4️⃣ Регистрация второго пользователя
        user2 = {"username": "bob", "password": "pass2", "age": 30, "gender": False}
        resp2 = requests.post(f"{self.BASE_URL}/register", json=user2)
        self.assertEqual(resp2.status_code, 200)
        user2_data = resp2.json()
        print("✅ User2 зарегистрирован:", user2_data)

        # 5️⃣ Логин второго пользователя
        login2 = {"name": "bob", "password": "pass2"}
        resp_login2 = requests.post(f"{self.BASE_URL}/login", json=login2)
        self.assertEqual(resp_login2.status_code, 200)
        token2 = resp_login2.json()["token"]

        # 6️⃣ Создание анкеты второго пользователя
        headers2 = {"Authorization": f"Bearer {token2}"}
        resp_quest2 = requests.post(f"{self.BASE_URL}/quest", json=quest_data1, headers=headers2)
        self.assertEqual(resp_quest2.status_code, 201)
        print("✅ User2 создал анкету")

        # 7️⃣ Получение списка анкет других пользователей (должен содержать анкету первого)
        user2_quests = requests.get(f"{self.BASE_URL}/quests", headers=headers2).json()
        self.assertTrue(len(user2_quests) > 0)
        active_quest_id = user2_quests[0]["id"]

        friends_list = requests.get(f"{self.BASE_URL}/quests/{active_quest_id}/friends", headers=headers2).json()
        self.assertTrue(len(friends_list) > 0)
        friend_quest_id = friends_list[0]["id"]
        print("✅ User2 получил список друзей:", [q["id"] for q in friends_list])

        # 8️⃣ Добавление анкеты первого пользователя в избранное второго
        resp_fav = requests.post(f"{self.BASE_URL}/quests/{active_quest_id}/fav/{friend_quest_id}", headers=headers2)
        self.assertEqual(resp_fav.status_code, 201)
        print(f"✅ User2 добавил анкету {friend_quest_id} в избранное")

if __name__ == "__main__":
    unittest.main()


if __name__ == "__main__":
    unittest.main()
