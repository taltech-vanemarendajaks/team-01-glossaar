INSERT INTO user_word_scores (user_id, word_id, quiz_score, last_quizzed_at)
SELECT w.user_id, w.id, 0, NULL
FROM words w
WHERE NOT EXISTS (
    SELECT 1 FROM user_word_scores uws
    WHERE uws.user_id = w.user_id
      AND uws.word_id = w.id
);
