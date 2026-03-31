ALTER TABLE users
ADD COLUMN auth_provider VARCHAR(255);

ALTER TABLE users
ADD COLUMN provider_id VARCHAR(255);

ALTER TABLE users
ADD CONSTRAINT uq_auth_provider UNIQUE (auth_provider, provider_id);

ALTER TABLE users
ALTER COLUMN email DROP NOT NULL;

ALTER TABLE users
ALTER COLUMN username DROP NOT NULL;