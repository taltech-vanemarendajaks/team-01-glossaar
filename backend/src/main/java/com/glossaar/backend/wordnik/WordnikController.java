package com.glossaar.backend.wordnik;

import com.glossaar.backend.wordnik.dto.WordnikExplanationsResponseDto;
import com.glossaar.backend.wordnik.dto.WordnikExplanationsResponseDto.ExplanationGroup;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/eki")
@RequiredArgsConstructor
public class WordnikController {

    private final WordnikService wordnikService;

    @GetMapping("/explanations/{word}")
    @Operation(summary = "Get Estonian explanations from EKI")
    public WordnikExplanationsResponseDto getExplanations(@PathVariable String word) {
        List<ExplanationGroup> explanationGroups = wordnikService.getExplanations(word);
        return new WordnikExplanationsResponseDto(word, explanationGroups);
    }
}
