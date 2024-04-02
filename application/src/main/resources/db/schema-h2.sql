CREATE TABLE
    IF NOT EXISTS runnables (
        id VARCHAR(255) NOT NULL PRIMARY KEY,
        created TIMESTAMP,
        updated TIMESTAMP,
        _data BINARY LARGE OBJECT,
        _clazz VARCHAR(255)
    );

CREATE INDEX runnables_id_index ON runnables (id, _clazz);