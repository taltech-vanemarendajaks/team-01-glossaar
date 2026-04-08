package com.glossaar.backend.auth;

import java.io.IOException;

import com.glossaar.backend.user.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.glossaar.backend.user.UserService;
import com.glossaar.backend.auth.jwt.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;

    @Value("${app.post-login-landing-url}")
    private String postLoginLandingUrl;

    public OAuthLoginSuccessHandler(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
        throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = token.getPrincipal();
        OAuthProvider provider = OAuthProvider.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());


        UserEntity userEntity = userService.upsertOAuth2User(user, provider);

        String jwt = jwtService.generateToken(userEntity.getId());

        Cookie cookie = new Cookie("auth_token", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 1 day
        // cookie.setSecure(true); // optional if using HTTPS
        response.addCookie(cookie);

        response.sendRedirect(postLoginLandingUrl);
    }
}
