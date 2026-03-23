package com.glossaar.backend.userword;

import com.glossaar.backend.user.UserEntity;
import com.glossaar.backend.word.WordEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "user_word_scores",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_word_score", columnNames = {"user_id", "word_id"})
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserWordScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private WordEntity word;

    @Column(name = "quiz_score", nullable = false)
    private int quizScore;

    public UserWordScoreEntity(UserEntity user, WordEntity word, int quizScore) {
        this.user = user;
        this.word = word;
        this.quizScore = quizScore;
    }
}
