# PPO_f/init_db.py
import os
import psycopg2
import time

def init_db():
    db_host = os.getenv("POSTGRES_HOST", "postgres")
    db_port = os.getenv("POSTGRES_PORT", "5432")
    db_user = os.getenv("POSTGRES_USER", "testuser")
    db_pass = os.getenv("POSTGRES_PASSWORD", "testpassword")
    db_name = os.getenv("POSTGRES_DB", "testdb")

    # Ждём пока PostgreSQL поднимется
    for _ in range(20):
        try:
            conn = psycopg2.connect(
                host=db_host,
                port=db_port,
                user=db_user,
                password=db_pass,
                database=db_name
            )
            print("PostgreSQL доступен")
            break
        except Exception:
            print("⏳ Ждём PostgreSQL...")
            time.sleep(3)
    else:
        raise Exception("Не удалось подключиться к PostgreSQL")

    cur = conn.cursor()
    print("🛠️  Создаём таблицы...")

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

    print("Заполняем тестовыми данными...")

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

    conn.commit()
    cur.close()
    conn.close()
    print("Инициализация БД завершена")


if __name__ == "__main__":
    init_db()
