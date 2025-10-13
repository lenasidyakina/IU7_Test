create table information_extended_answers
(
    information_id      bigint not null
        constraint fk86uy93eb2bp84flk3l7io2ufd
            references information,
    extended_answers_id bigint not null
        constraint uk3ao7h9qc1rb1x8pjgpykk6sk5
            unique
        constraint fk9i5xrlaob1v0m21px1aghex5e
            references extended_answer
);
