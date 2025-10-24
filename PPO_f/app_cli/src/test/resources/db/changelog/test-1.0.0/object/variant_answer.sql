create table variant_answer
(
    id          BIGSERIAL PRIMARY KEY,
    weight      integer not null,
    question_id bigint
        constraint fk8fanrcoi3c7p3rrwksoajbucf
            references question,
    tag_id      bigint
        constraint fk5nwapf4dks98xealgxmt9jux1
            references tag
);