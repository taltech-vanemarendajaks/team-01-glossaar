
package com.glossaar.backend.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.glossaar.backend.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Value("${app.post-login-landing-url}")
    private String postLoginLandingUrl;

    public OAuthLoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        OAuth2User user = (OAuth2User) token.getPrincipal();
        OAuthProvider provider = OAuthProvider.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());

        userService.upsertOAuth2User(user, provider);

        response.sendRedirect(postLoginLandingUrl);
    }
}