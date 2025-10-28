create table questionnaire_fav_list
(
    questionnaire_id bigint not null
        constraint fkqds0bby62nrt9wn2mdf7uyqgm
            references questionnaire,
    fav_list_id      bigint not null
        constraint ukcj87diychd0me9iqqb2ne3rm6
            unique
        constraint fkik6axe2tshp5udueq50p8whhd
            references questionnaire
);