create table if not exists public.runnable
(
    id      varchar(255) not null
        primary key,
    created timestamp(6) with time zone default CURRENT_TIMESTAMP,
    data    bytea,
    clazz   varchar(255) not null,
    updated timestamp(6) with time zone default CURRENT_TIMESTAMP
);

-- alter table public.runnable
--     owner to postgres;

