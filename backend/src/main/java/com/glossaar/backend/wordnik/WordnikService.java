package com.glossaar.backend.wordnik;

import com.glossaar.backend.wordnik.dto.WordnikDefinitionResponse;
import com.glossaar.backend.wordnik.dto.WordnikExplanationsResponseDto.ExplanationGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordnikService {

    private final RestClient wordnikRestClient;

    public List<ExplanationGroup> getExplanations(String word) {
        List<WordnikDefinitionResponse> definitions;

        try {
            definitions = wordnikRestClient.get()
                .uri("/word.json/{word}/definitions", word)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.warn("Wordnik definitions fetch failed for '{}': {}", word, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Wordnik service unavailable");
        }

        if (definitions == null || definitions.isEmpty()) {
            return List.of();
        }

        Map<String, List<String>> grouped = new LinkedHashMap<>();
        List<String> ungrouped = new ArrayList<>();

        for (WordnikDefinitionResponse def : definitions) {
            if (def.text() == null || def.text().isBlank()) continue;
            String pos = def.partOfSpeech();
            if (pos == null || pos.isBlank()) {
                ungrouped.add(def.text());
            } else {
                grouped.computeIfAbsent(pos, k -> new ArrayList<>()).add(def.text());
            }
        }

        List<ExplanationGroup> result = new ArrayList<>();
        int groupNumber = 1;
        for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
            result.add(new ExplanationGroup(groupNumber++, entry.getValue()));
        }

        if (!ungrouped.isEmpty()) {
            if (result.isEmpty()) {
                result.add(new ExplanationGroup(1, ungrouped));
            } else {
                List<String> lastExplanations = new ArrayList<>(result.getLast().explanations());
                lastExplanations.addAll(ungrouped);
                result.set(result.size() - 1, new ExplanationGroup(result.getLast().groupNumber(), lastExplanations));
            }
        }

        return result;
    }
}
