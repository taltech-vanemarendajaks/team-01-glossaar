package com.glossaar.backend.auth.dto;

import com.glossaar.backend.auth.OAuthProvider;
import com.glossaar.backend.user.dto.UserResponseDto;

import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;

public record MeResponseDto(
    @Schema(description = "User response DTO") UserResponseDto user,
    @Schema(description = "Auth provider", example = "GOOGLE") OAuthProvider authProvider,
    @Schema(description = "Profile picture", example = "example.com/profile.jpg") Optional<String> avatarUrl) {
}
