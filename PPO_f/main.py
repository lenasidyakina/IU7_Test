import unittest
import os
import psycopg2
import time
import requests

class TestE2E_API(unittest.TestCase):
    BASE_URL = "http://localhost:9099/api/v1"

    def setUp(self):
        self.BASE_URL = "http://localhost:9099/api/v1"
        # Ждём, пока API поднимется
        for _ in range(20):
            try:
                r = requests.get(f"{self.BASE_URL}/users", timeout=2)
                if r.status_code in (200, 404):
                    print("API доступен")
                    break
            except Exception:
                print("Ждём API...")
                time.sleep(3)
        else:
            raise Exception("API не ответил")


    def tearDown(self):
        if hasattr(self, "conn"):
            self.conn.close()

    def test_two_users_quest_fav(self):
        # Регистрация первого пользователя
        user1 = {"username": "alice", "password": "pass1", "age": 25, "gender": True}
        resp1 = requests.post(f"{self.BASE_URL}/register", json=user1)
        self.assertEqual(resp1.status_code, 200)
        user1_data = resp1.json()
        print("User1 зарегистрирован:", user1_data)

        # Логин первого пользователя
        login1 = {"name": "alice", "password": "pass1"}
        resp_login1 = requests.post(f"{self.BASE_URL}/login", json=login1)
        self.assertEqual(resp_login1.status_code, 200)
        token1 = resp_login1.json()["token"]

        # Создание анкеты первого пользователя
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
        print("User1 создал анкету")

        # Регистрация второго пользователя
        user2 = {"username": "bob", "password": "pass2", "age": 30, "gender": False}
        resp2 = requests.post(f"{self.BASE_URL}/register", json=user2)
        self.assertEqual(resp2.status_code, 200)
        user2_data = resp2.json()
        print("User2 зарегистрирован:", user2_data)

        # Логин второго пользователя
        login2 = {"name": "bob", "password": "pass2"}
        resp_login2 = requests.post(f"{self.BASE_URL}/login", json=login2)
        self.assertEqual(resp_login2.status_code, 200)
        token2 = resp_login2.json()["token"]

        # Создание анкеты второго пользователя
        headers2 = {"Authorization": f"Bearer {token2}"}
        resp_quest2 = requests.post(f"{self.BASE_URL}/quest", json=quest_data1, headers=headers2)
        self.assertEqual(resp_quest2.status_code, 201)
        print("User2 создал анкету")

        # Получение списка анкет других пользователей
        user2_quests = requests.get(f"{self.BASE_URL}/quests", headers=headers2).json()
        self.assertTrue(len(user2_quests) > 0)
        active_quest_id = user2_quests[0]["id"]

        friends_list = requests.get(f"{self.BASE_URL}/quests/{active_quest_id}/friends", headers=headers2).json()
        self.assertTrue(len(friends_list) > 0)
        friend_quest_id = friends_list[0]["id"]
        print("User2 получил список друзей:", [q["id"] for q in friends_list])

        # Добавление анкеты первого пользователя в избранное второго
        resp_fav = requests.post(f"{self.BASE_URL}/quests/{active_quest_id}/fav/{friend_quest_id}", headers=headers2)
        self.assertEqual(resp_fav.status_code, 201)
        print(f"User2 добавил анкету {friend_quest_id} в избранное")

if __name__ == "__main__":
    unittest.main()
