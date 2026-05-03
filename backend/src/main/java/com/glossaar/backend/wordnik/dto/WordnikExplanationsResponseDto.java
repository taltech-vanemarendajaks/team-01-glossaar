package com.glossaar.backend.wordnik.dto;

import java.util.List;

public record WordnikExplanationsResponseDto(String word, List<ExplanationGroup> explanationGroups) {
    public record ExplanationGroup(int groupNumber, List<String> explanations) {}
}
