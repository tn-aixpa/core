CREATE TABLE
    IF NOT EXISTS runnables (
        id VARCHAR(255) NOT NULL PRIMARY KEY,
        _user VARCHAR(255),
        created TIMESTAMP,
        updated TIMESTAMP,
        _clazz VARCHAR(255),
        _data BINARY LARGE OBJECT
    );

CREATE INDEX IF NOT EXISTS runnables_id_index ON runnables (id, _clazz);