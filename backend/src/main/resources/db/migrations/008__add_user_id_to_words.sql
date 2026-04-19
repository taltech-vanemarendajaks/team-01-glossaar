-- 1. Add column (nullable for now)
ALTER TABLE words ADD COLUMN user_id BIGINT;

-- 2. Populate existing rows
UPDATE words SET user_id = 1;

-- 3. Add FK constraint
ALTER TABLE words
    ADD CONSTRAINT fk_words_user
        FOREIGN KEY (user_id) REFERENCES users(id);

-- 4. Make column NOT NULL
ALTER TABLE words
    ALTER COLUMN user_id SET NOT NULL;

-- 5. Add index
CREATE INDEX idx_words_user_id ON words(user_id);
