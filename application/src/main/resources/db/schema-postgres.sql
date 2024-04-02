CREATE TABLE
    IF NOT EXISTS public.runnables (
        id VARCHAR(255) NOT NULL PRIMARY KEY,
        created TIMESTAMP(6) WITH TIME ZONE,
        updated TIMESTAMP(6) WITH TIME ZONE,
        _clazz VARCHAR(255) NOT NULL,
        _data BYTEA
    );

CREATE INDEX runnables_id_index ON public.runnables (id, _clazz);

-- alter table public.runnable
--     owner to postgres;