package com.glossaar.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(
        @NotBlank(message = "username must not be blank")
        @Size(max = 255, message = "username must be at most 255 characters")
        @Schema(description = "Unique username.", example = "TestUser")
        String username
) {
}
