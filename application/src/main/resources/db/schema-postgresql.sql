CREATE TABLE
    IF NOT EXISTS public.runnables (
        id VARCHAR(255) NOT NULL PRIMARY KEY,
        _user VARCHAR(255),
        created TIMESTAMP(6) WITH TIME ZONE,
        updated TIMESTAMP(6) WITH TIME ZONE,
        _clazz VARCHAR(255) NOT NULL,
        _data BYTEA
    );

CREATE INDEX IF NOT EXISTS runnables_id_index ON public.runnables (id, _clazz);

-- alter table public.runnable
--     owner to postgres;