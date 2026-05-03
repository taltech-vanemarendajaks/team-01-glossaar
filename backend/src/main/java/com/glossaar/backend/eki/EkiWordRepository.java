package com.glossaar.backend.eki;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EkiWordRepository extends JpaRepository<EkiWordEntity, Long> {
    List<EkiWordEntity> findAllByWordNormalizedAndLangOrderByHomonymNrAscIdAsc(String wordNormalized, String lang);
}
