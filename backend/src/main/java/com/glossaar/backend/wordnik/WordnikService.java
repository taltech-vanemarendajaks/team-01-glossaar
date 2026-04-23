package com.glossaar.backend.wordnik;

import com.glossaar.backend.wordnik.dto.EkiExplanationsResponseDto.ExplanationGroup;
import com.glossaar.backend.wordnik.dto.EkiSearchResponse;
import com.glossaar.backend.wordnik.dto.EkiSearchResponse.EkiWord;
import com.glossaar.backend.wordnik.dto.EkiWordDetailsResponse;
import com.glossaar.backend.wordnik.dto.EkiWordDetailsResponse.EkiLexeme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordnikService {

    private final RestClient ekiRestClient;


    public List<WordnikExplanationGroup> getExplanations(String word) {
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
            if (!"est".equals(ekiWord.lang())) continue;
            List<String> explanations = getExplanationsForWord(ekiWord.wordId());
            if (!explanations.isEmpty()) {
                explanationGroups.add(new ExplanationGroup(ekiWord.homonymNr(), explanations));
            }
        }
        return explanationGroups;
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
            .filter(group -> "est".equals(group.lang()) && group.definitions() != null)
            .flatMap(group -> group.definitions().stream())
            .map(EkiWordDetailsResponse.Definition::value)
            .filter(value -> value != null && !value.isBlank())
            .findFirst();
    }
}
