create table extended_answer_tags
(
    extended_answer_id bigint not null
        constraint fkeoomyj1bjvso8dpuytwrc9x5l
            references extended_answer,
    tags_id            bigint not null
        constraint fkp4x72kpxvn7dvpikebf5d21pv
            references tag,
    constraint ukgsa0yo0c2jtbfk351rvvr9h0t
        unique (tags_id, extended_answer_id)
);
