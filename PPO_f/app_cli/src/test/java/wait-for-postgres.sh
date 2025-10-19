#!/bin/bash
set -e

# Переменные из Docker Compose
: "${POSTGRES_HOST:=postgres}"
: "${POSTGRES_PORT:=5432}"
: "${POSTGRES_USER:=testuser}"
: "${POSTGRES_PASSWORD:=testpassword}"

echo "Waiting for PostgreSQL at $POSTGRES_HOST:$POSTGRES_PORT..."

until PGPASSWORD=$POSTGRES_PASSWORD psql -h "$POSTGRES_HOST" -U "$POSTGRES_USER" -d testdb -c '\q' 2>/dev/null; do
  echo "Postgres is unavailable - sleeping"
  sleep 2
done

echo "Postgres is up - running tests"

# Запускаем Gradle тесты
./gradlew :app_cli:test --stacktrace --info
