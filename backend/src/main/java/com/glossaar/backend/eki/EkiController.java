package com.glossaar.backend.eki;

import com.glossaar.backend.eki.dto.EkiExplanationsResponseDto;
import com.glossaar.backend.eki.dto.EkiExplanationsResponseDto.ExplanationGroup;
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
public class EkiController {

    private final EkiService ekiService;

    @GetMapping("/explanations/{word}")
    @Operation(summary = "Get Estonian explanations from EKI")
    public EkiExplanationsResponseDto getExplanations(@PathVariable String word) {
        List<ExplanationGroup> explanationGroups = ekiService.getExplanations(word);
        return new EkiExplanationsResponseDto(word, explanationGroups);
    }
}
