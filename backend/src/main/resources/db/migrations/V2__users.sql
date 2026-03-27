-- Just an example migration. Will be updated once the requirements clarify
CREATE TABLE users (
    id BIGINT PRIMARY KEY NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
