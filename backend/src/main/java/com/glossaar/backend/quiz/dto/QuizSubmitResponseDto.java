package com.glossaar.backend.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record QuizSubmitResponseDto(
        @Schema(description = "Submission result message.", example = "ok")
        String message
) {
}
