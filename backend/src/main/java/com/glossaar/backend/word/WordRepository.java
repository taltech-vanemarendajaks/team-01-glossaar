package com.glossaar.backend.word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WordRepository extends JpaRepository<WordEntity, Long> {
    Page<WordEntity> findByWordContainingIgnoreCaseOrExplanationContainingIgnoreCase(
            String wordFilter,
            String explanationFilter,
            Pageable pageable
    );
}
