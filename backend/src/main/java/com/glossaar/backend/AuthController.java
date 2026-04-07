package com.glossaar.backend;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glossaar.backend.auth.OAuthProvider;
import com.glossaar.backend.auth.dto.MeResponseDto;
import com.glossaar.backend.user.UserService;
import com.glossaar.backend.user.dto.UserResponseDto;
import com.glossaar.backend.docs.UnauthorizedApiResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/me")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User info fetched successfully",
            content = @Content(schema = @Schema(implementation = MeResponseDto.class))
        )
    })
    @UnauthorizedApiResponse
    public MeResponseDto getMe(Authentication principal) {

        OAuth2User user = (OAuth2User) principal.getPrincipal();
        String authProviderUserId = user.getName();
        OAuthProvider provider = getAuthProvider(principal);

        UserResponseDto userResponseDto = userService.getByOAuthAccount(provider, authProviderUserId);
        Optional<String> profilePictureUrl = getProfilePictureUrl(user, provider);

        MeResponseDto resp = new MeResponseDto(userResponseDto, provider, profilePictureUrl);

        return resp;
    }

    private OAuthProvider getAuthProvider(Authentication principal) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
        return OAuthProvider.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());
    }

    private Optional<String> getProfilePictureUrl(OAuth2User user, OAuthProvider provider) {
        Map<String, Object> attributes = user.getAttributes();

        return switch (provider) {
            case OAuthProvider.GOOGLE -> Optional.ofNullable((String) attributes.get("picture"));
            case OAuthProvider.GITHUB -> Optional.ofNullable((String) attributes.get("avatar_url"));
            default -> Optional.empty();
        };
    }
}
