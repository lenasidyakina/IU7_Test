
create table question
(
    id   BIGSERIAL PRIMARY KEY,
    is_extended boolean not null,
    question    TEXT
);