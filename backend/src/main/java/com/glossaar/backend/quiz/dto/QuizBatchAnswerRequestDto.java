package com.glossaar.backend.quiz.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record QuizBatchAnswerRequestDto(
        @NotNull(message = "userId is required")
        @Schema(description = "User ID submitting the answers.", example = "1")
        Long userId,
        @NotNull(message = "answers are required")
        @Size(min = 1, message = "answers must contain at least 1 item")
        @ArraySchema(
                minItems = 1,
                schema = @Schema(implementation = QuizAnswerItemDto.class)
        )
        List<@Valid QuizAnswerItemDto> answers
) {
    public record QuizAnswerItemDto(
            @NotBlank(message = "word is required")
            @Schema(description = "Word answered by the user.", example = "cillum-2")
            String word,
            @NotNull(message = "correct is required")
            @Schema(description = "Whether user's answer was correct.", example = "true")
            Boolean correct
    ) {
    }
}
