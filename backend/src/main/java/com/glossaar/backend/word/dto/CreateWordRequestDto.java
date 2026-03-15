package com.glossaar.backend.word.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWordRequestDto(
        @NotBlank(message = "word must not be blank")
        @Size(max = 255, message = "word must be at most 255 characters")
        String word,
        @Size(max = 1000, message = "explanation must be at most 1000 characters")
        String explanation
) {
}
