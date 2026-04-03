ALTER TABLE user_word_scores
ADD COLUMN last_quizzed_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_user_word_scores_user_last_quizzed_at
ON user_word_scores (user_id, last_quizzed_at);
