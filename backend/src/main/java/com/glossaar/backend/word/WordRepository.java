package com.glossaar.backend.word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<WordEntity, Long> {
    Page<WordEntity> findByWordContainingIgnoreCaseOrExplanationContainingIgnoreCase(
            String wordFilter,
            String explanationFilter,
            Pageable pageable
    );

    @Query("SELECT w FROM WordEntity w WHERE w.category.id IN :categoryIds")
    List<WordEntity> findAllByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    long countByCategory_Id(Long categoryId);
}
