ALTER TABLE users
ALTER COLUMN email DROP NOT NULL;

ALTER TABLE users
ALTER COLUMN username DROP NOT NULL;


CREATE TABLE user_auth_providers (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider_name VARCHAR(255) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    CONSTRAINT uq_auth_provider UNIQUE (provider_name, provider_user_id),
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);