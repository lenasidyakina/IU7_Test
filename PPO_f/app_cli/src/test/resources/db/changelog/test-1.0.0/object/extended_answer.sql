create table extended_answer
(
    id   BIGSERIAL PRIMARY KEY,
    answer  TEXT,
    weight      integer not null,
    question_id bigint
        constraint fkcdfd08mr068sm8uxqpdjaypp6
            references question
);