create table if not exists customers (
    id bigserial not null,
    name text not null,
    email text not null,
    primary key (id),
    UNIQUE (email)
);
