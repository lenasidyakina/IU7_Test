#!/bin/bash
set -e

: "${POSTGRES_HOST:=postgres}"
: "${POSTGRES_PORT:=5432}"
: "${POSTGRES_USER:=testuser}"
: "${POSTGRES_PASSWORD:=testpassword}"
: "${POSTGRES_DB:=testdb}"

echo "Waiting for PostgreSQL at $POSTGRES_HOST:$POSTGRES_PORT..."

until PGPASSWORD=$POSTGRES_PASSWORD psql -h "$POSTGRES_HOST" -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c '\q' 2>/dev/null; do
  echo "Postgres is unavailable - sleeping 2s..."
  sleep 2
done

echo "Postgres is up - sleeping 2s to let DB initialize"
sleep 2

echo "Running Gradle tests..."
./gradlew :app_cli:test --stacktrace --info
