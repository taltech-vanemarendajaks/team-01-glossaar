package com.glossaar.backend.word;

import com.glossaar.backend.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WordRepository extends JpaRepository<WordEntity, Long> {

    @Query("""
    SELECT w FROM WordEntity w
    WHERE w.category.id IN :categoryIds
    AND w.user = :user
""")
    List<WordEntity> findAllByCategoryIdsAndUser(
        @Param("categoryIds") List<Long> categoryIds,
        @Param("user") UserEntity user
    );

    long countByCategory_IdAndUser(Long categoryId, UserEntity user);

    Page<WordEntity> findAllByUser(UserEntity user, Pageable pageable);

    Optional<WordEntity> findByIdAndUser(Long id, UserEntity user);

    Page<WordEntity> findByUserAndWordContainingIgnoreCaseOrUserAndExplanationContainingIgnoreCase(
        UserEntity user1,
        String wordFilter,
        UserEntity user2,
        String explanationFilter,
        Pageable pageable
    );

}
