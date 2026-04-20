package com.glossaar.backend.auth;

import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

@Component
public class AuthLogoutSuccessHandler implements LogoutSuccessHandler  {
    @Value("${app.post-logout-landing-url}")
    private String postLogoutLandingUrl;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
            // The JWT itself will remain valid until its TTL expires. Invalidate the cookie to kill the session on this client.
            response.setHeader("Set-Cookie", "auth_token=; HttpOnly; Path=/; Max-Age=0");
            response.sendRedirect(postLogoutLandingUrl);
    };
}
