create table information_variant_answers
(
    information_id     bigint not null
        constraint fk8y26fn68jreox5b5v7rrfj9vk
            references information,
    variant_answers_id bigint not null
        constraint uknx1mbce2ehbynjpp9fia38nhh
            unique
        constraint fke66rrhk6xcgq7t8ecotb8m2eu
            references variant_answer
);
