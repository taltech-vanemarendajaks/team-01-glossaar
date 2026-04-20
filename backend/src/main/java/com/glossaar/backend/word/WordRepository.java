package com.glossaar.backend.word;

import com.glossaar.backend.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<WordEntity, Long> {

    @Query("SELECT w FROM WordEntity w WHERE w.category.id IN :categoryIds")
    List<WordEntity> findAllByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    long countByCategory_Id(Long categoryId);

    Page<WordEntity> findAllByUser(UserEntity user, Pageable pageable);

    Page<WordEntity> findByUserAndWordContainingIgnoreCaseOrUserAndExplanationContainingIgnoreCase(
        UserEntity user1,
        String wordFilter,
        UserEntity user2,
        String explanationFilter,
        Pageable pageable
    );
}
