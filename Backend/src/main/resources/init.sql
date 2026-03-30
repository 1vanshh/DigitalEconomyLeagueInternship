CREATE TABLE IF NOT EXISTS client_account (
    id BIGINT PRIMARY KEY NOT NULL,
    full_name TEXT NOT NULL,
    gender VARCHAR(1),
    status VARCHAR(20),
    create_dttm TIMESTAMP WITH TIME ZONE NOT NULL,
    modify_dttm TIMESTAMP WITH TIME ZONE NOT NULL
    );