package com.glossaar.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponseDto(
        @Schema(description = "User ID.", example = "1") Long id,
        @Schema(description = "Unique username.", example = "TestUser") String username) {
}
