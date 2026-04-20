-- 1. Add column (nullable for now)
ALTER TABLE categories ADD COLUMN user_id BIGINT;

-- 2. Populate existing rows (IMPORTANT: choose correct owner)
UPDATE categories SET user_id = 1;

-- 3. Add FK constraint
ALTER TABLE categories
    ADD CONSTRAINT fk_categories_user
        FOREIGN KEY (user_id) REFERENCES users(id);

-- 4. Make column NOT NULL (only if every row has a valid user_id)
ALTER TABLE categories
    ALTER COLUMN user_id SET NOT NULL;

-- 5. Add index for performance
CREATE INDEX idx_categories_user_id ON categories(user_id);
