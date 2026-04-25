ALTER TABLE user_word_scores
    DROP CONSTRAINT fk_user_word_scores_user;

ALTER TABLE user_word_scores
    ADD CONSTRAINT fk_user_word_scores_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE user_word_scores
    DROP CONSTRAINT fk_user_word_scores_word;

ALTER TABLE user_word_scores
    ADD CONSTRAINT fk_user_word_scores_word
        FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE;
