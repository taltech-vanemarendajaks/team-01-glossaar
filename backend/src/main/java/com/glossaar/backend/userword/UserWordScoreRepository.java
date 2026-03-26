package com.glossaar.backend.userword;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserWordScoreRepository extends JpaRepository<UserWordScoreEntity, Long> {
    Optional<UserWordScoreEntity> findByUserIdAndWordId(Long userId, Long wordId);

    List<UserWordScoreEntity> findAllByUserId(Long userId);
}
