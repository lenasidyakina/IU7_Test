-- Таблицы (если их ещё нет)
create table IF NOT EXISTS question
(
    id   BIGSERIAL PRIMARY KEY,
    is_extended boolean not null,
    question    TEXT
);

create table IF NOT EXISTS tag
(
    id   BIGSERIAL PRIMARY KEY,
    name TEXT
);



CREATE TABLE IF NOT EXISTS question_tags (
    question_id BIGSERIAL not null
        constraint fkf76giw3qwi7ooxeims83jp29k
            references question,
    tags_id     BIGSERIAL not null
        constraint uknjqlxjyums5xk6r7gxhxni0fj
            unique
        constraint fkgs9vxcbqngkcv0ow6leux7erp
            references tag
);

-- Данные из твоего XML
INSERT INTO question(id, is_extended, question) VALUES
(0, TRUE, 'Do you love swimming or watching TV?'),
(1, FALSE, 'How do you like to spend your time?');

INSERT INTO tag(id, name) VALUES
(0, 'walking'),
(1, 'watching TV'),
(2, 'swimming'),
(3, 'sleeping');

INSERT INTO question_tags(question_id, tags_id) VALUES
(0, 1),
(0, 2),
(1, 0),
(1, 3);
