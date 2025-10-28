create table questionnaire_black_list
(
    questionnaire_id bigint not null
        constraint fk585l20joecvy565pha4f9190v
            references questionnaire,
    black_list_id    bigint not null
        constraint ukjg8o74n78n0p8ndwqrod3n34l
            unique
        constraint fkqeb38qlcmcla6oo7h5i67s88b
            references questionnaire
);