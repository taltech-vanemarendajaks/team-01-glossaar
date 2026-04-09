package com.glossaar.backend;

import java.util.Optional;

import com.glossaar.backend.auth.OAuthAccount;
import com.glossaar.backend.user.UserEntity;
import com.glossaar.backend.user.UserPrincipal;
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

        Object principalObj = principal.getPrincipal();
        UserResponseDto userResponseDto;
        OAuthProvider provider;

        if (principalObj instanceof OAuth2User oauthUser) {
            String authProviderUserId = oauthUser.getName();
            provider = getAuthProvider(principal);
            userResponseDto = userService.getByOAuthAccount(provider, authProviderUserId);
        } else if (principalObj instanceof UserPrincipal userPrincipal) {
            UserEntity userEntity = userPrincipal.getUser();
            userResponseDto = new UserResponseDto(userEntity.getId(), userEntity.getUsername());
        } else {
            throw new IllegalStateException("Unknown principal type: " + principalObj.getClass());
        }

        Optional<OAuthAccount> accountOption = userService.getOAuthAccount(userResponseDto.id());
        Optional<String> profilePictureUrl = accountOption.map(OAuthAccount::getAvatarUrl);
        provider = accountOption.map(OAuthAccount::getProviderName).orElse(null);

        return new MeResponseDto(userResponseDto, provider, profilePictureUrl);
    }

    private OAuthProvider getAuthProvider(Authentication principal) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
        return OAuthProvider.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());
    }
}
