package com.glossaar.backend.eki.dto;

import java.util.List;

public record EkiExplanationsResponseDto(String word, List<ExplanationGroup> explanationGroups) {
    public record ExplanationGroup(int groupNumber, List<String> explanations) {}
}
