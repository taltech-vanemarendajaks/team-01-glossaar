package com.glossaar.backend.userword;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserWordScoreRepository extends JpaRepository<UserWordScoreEntity, Long> {
    Optional<UserWordScoreEntity> findByUserIdAndWordId(Long userId, Long wordId);

    List<UserWordScoreEntity> findAllByUserId(Long userId);

    @Modifying
    @Query("""
            update UserWordScoreEntity u
            set u.quizScore = u.quizScore + :delta,
                u.lastQuizzedAt = :now
            where u.user.id = :userId
              and u.word.id = :wordId
            """)
    int incrementScoreAndTouch(
            @Param("userId") Long userId,
            @Param("wordId") Long wordId,
            @Param("delta") int delta,
            @Param("now") LocalDateTime now
    );
}
