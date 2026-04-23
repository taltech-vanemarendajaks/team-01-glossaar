package com.glossaar.backend.eki;

import com.glossaar.backend.eki.dto.EkiExplanationsResponseDto.ExplanationGroup;
import com.glossaar.backend.eki.dto.EkiSearchResponse;
import com.glossaar.backend.eki.dto.EkiSearchResponse.EkiWord;
import com.glossaar.backend.eki.dto.EkiWordDetailsResponse;
import com.glossaar.backend.eki.dto.EkiWordDetailsResponse.EkiLexeme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EkiService {
    private static final String ESTONIAN_LANG = "est";

    private final RestClient ekiRestClient;
    private final EkiWordRepository ekiWordRepository;

    public List<ExplanationGroup> getExplanations(String word) {
        String requestedWord = word == null ? "" : word.trim();
        String normalizedWord = normalizeWord(requestedWord);
        if (normalizedWord.isEmpty()) {
            return List.of();
        }

        List<ExplanationGroup> cachedExplanations = getCachedExplanations(normalizedWord);
        if (!cachedExplanations.isEmpty()) {
            log.info("EKI explanation source=db word='{}' groups={}", normalizedWord, cachedExplanations.size());
            return cachedExplanations;
        }

        List<ExplanationGroup> fetchedExplanations = fetchExplanationsFromEki(normalizedWord);
        if (!fetchedExplanations.isEmpty()) {
            saveCachedExplanations(requestedWord, normalizedWord, fetchedExplanations);
        }
        log.info("EKI explanation source=eki word='{}' groups={}", normalizedWord, fetchedExplanations.size());
        return fetchedExplanations;
    }

    private List<ExplanationGroup> getCachedExplanations(String normalizedWord) {
        List<EkiWordEntity> cachedRows =
                ekiWordRepository.findAllByWordNormalizedAndLangOrderByHomonymNrAscIdAsc(normalizedWord, ESTONIAN_LANG);
        if (cachedRows.isEmpty()) {
            return List.of();
        }

        Map<Integer, List<String>> grouped = new LinkedHashMap<>();
        for (EkiWordEntity cachedRow : cachedRows) {
            grouped.computeIfAbsent(cachedRow.getHomonymNr(), ignored -> new ArrayList<>()).add(cachedRow.getExplanation());
        }

        List<ExplanationGroup> explanationGroups = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : grouped.entrySet()) {
            explanationGroups.add(new ExplanationGroup(entry.getKey(), entry.getValue()));
        }
        return explanationGroups;
    }

    private List<ExplanationGroup> fetchExplanationsFromEki(String word) {
        EkiSearchResponse response;
        try {
            response = ekiRestClient.get()
                .uri("/api/word/search/{word}", word)
                .retrieve()
                .body(EkiSearchResponse.class);
        } catch (Exception e) {
            log.warn("EKI word search failed for '{}': {}", word, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "EKI service unavailable");
        }

        if (response == null || response.words() == null) {
            return List.of();
        }

        List<ExplanationGroup> explanationGroups = new ArrayList<>();
        for (EkiWord ekiWord : response.words()) {
            if (!ESTONIAN_LANG.equals(ekiWord.lang())) continue;
            List<String> explanations = getExplanationsForWord(ekiWord.wordId());
            if (!explanations.isEmpty()) {
                explanationGroups.add(new ExplanationGroup(ekiWord.homonymNr(), explanations));
            }
        }
        return explanationGroups;
    }

    private void saveCachedExplanations(String requestedWord, String normalizedWord, List<ExplanationGroup> explanationGroups) {
        List<EkiWordEntity> rowsToSave = new ArrayList<>();
        Set<String> uniquePerBatch = new HashSet<>();
        for (ExplanationGroup group : explanationGroups) {
            for (String explanation : group.explanations()) {
                if (explanation == null || explanation.isBlank()) {
                    continue;
                }
                String dedupeKey = group.groupNumber() + "::" + explanation.trim();
                if (!uniquePerBatch.add(dedupeKey)) {
                    continue;
                }

                EkiWordEntity entity = new EkiWordEntity();
                entity.setWord(requestedWord);
                entity.setWordNormalized(normalizedWord);
                entity.setLang(ESTONIAN_LANG);
                entity.setHomonymNr(group.groupNumber());
                entity.setExplanation(explanation.trim());
                rowsToSave.add(entity);
            }
        }

        if (rowsToSave.isEmpty()) {
            return;
        }

        try {
            ekiWordRepository.saveAll(rowsToSave);
        } catch (DataIntegrityViolationException e) {
            // Duplicate insertions can happen under concurrent requests for the same word.
            log.debug("EKI cache write conflict for word='{}': {}", normalizedWord, e.getMessage());
        }
    }

    private List<String> getExplanationsForWord(long wordId) {
        try {
            EkiWordDetailsResponse response = ekiRestClient.get()
                .uri("/api/word/details/{wordId}", wordId)
                .retrieve()
                .body(EkiWordDetailsResponse.class);

            if (response == null || response.lexemes() == null) {
                return List.of();
            }

            Set<Integer> usedLevels = new HashSet<>();
            List<String> explanations = new ArrayList<>();

            for (EkiLexeme ekiLexeme : response.lexemes()) {
                if (!"eki".equals(ekiLexeme.datasetCode())) continue;
                if (usedLevels.contains(ekiLexeme.level1())) continue;
                usedLevels.add(ekiLexeme.level1());
                findEstonianExplanation(ekiLexeme).ifPresent(explanations::add);
            }
            return explanations;
        } catch (RestClientException e) {
            log.warn("EKI word details fetch failed for wordId {}: {}", wordId, e.getMessage());
            return List.of();
        }
    }

    private Optional<String> findEstonianExplanation(EkiLexeme lexeme) {
        if (lexeme.meaning() == null || lexeme.meaning().definitionLangGroups() == null) {
            return Optional.empty();
        }
        return lexeme.meaning().definitionLangGroups().stream()
            .filter(group -> ESTONIAN_LANG.equals(group.lang()) && group.definitions() != null)
            .flatMap(group -> group.definitions().stream())
            .map(EkiWordDetailsResponse.Definition::value)
            .filter(value -> value != null && !value.isBlank())
            .findFirst();
    }

    private static String normalizeWord(String word) {
        return word.trim().toLowerCase(Locale.ROOT);
    }
}
