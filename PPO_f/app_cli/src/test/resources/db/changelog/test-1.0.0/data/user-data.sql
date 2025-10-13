-- liquibase formatted sql

--changeset lena:user-data-1
INSERT INTO users(age, gender, name, password, role)
VALUES (11, true, 'user', 'test', 'admin');