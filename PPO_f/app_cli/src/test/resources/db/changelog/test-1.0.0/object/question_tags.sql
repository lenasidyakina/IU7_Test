create table question_tags
(
    question_id BIGSERIAL not null
        constraint fkf76giw3qwi7ooxeims83jp29k
            references question,
    tags_id     BIGSERIAL not null
        constraint uknjqlxjyums5xk6r7gxhxni0fj
            unique
        constraint fkgs9vxcbqngkcv0ow6leux7erp
            references tag
);


