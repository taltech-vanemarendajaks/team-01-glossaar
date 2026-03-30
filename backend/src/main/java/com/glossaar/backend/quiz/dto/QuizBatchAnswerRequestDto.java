package com.glossaar.backend.quiz.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record QuizBatchAnswerRequestDto(
        @NotNull(message = "userId is required")
        @Schema(description = "User ID submitting the answers.", example = "1")
        Long userId,
        @NotNull(message = "answers are required")
        @Size(min = 1, max = 100, message = "answers must contain between 1 and 100 items")
        @ArraySchema(
                minItems = 1,
                maxItems = 100,
                schema = @Schema(implementation = QuizAnswerItemDto.class)
        )
        List<@Valid QuizAnswerItemDto> answers
) {
    public record QuizAnswerItemDto(
            @NotNull(message = "wordId is required")
            @Schema(description = "Word ID being answered.", example = "42")
            Long wordId,
            @NotNull(message = "correct is required")
            @Schema(description = "Whether user's answer was correct.", example = "true")
            Boolean correct
    ) {
    }
}
