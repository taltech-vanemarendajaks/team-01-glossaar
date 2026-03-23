package com.glossaar.backend.quiz.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record QuizQuestionResponseDto(
        @Schema(
                description = "Word that should be matched with the correct explanation.",
                example = "cillum-2"
        )
        String word,
        @ArraySchema(
                minItems = 4,
                maxItems = 4,
                schema = @Schema(
                        description = "Possible explanations (one is correct).",
                        example = "Lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor."
                )
        )
        List<String> options,
        @Schema(
                description = "Zero-based index of the correct explanation in options.",
                example = "2",
                minimum = "0",
                maximum = "3"
        )
        int correctIndex
) {
}
