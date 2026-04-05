package com.glossaar.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.glossaar.backend.auth.OAuthLoginSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuthLoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(OAuthLoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO: Set up oauth2 login with server
        // TODO: replace JSESSIONID cookie with JWT token, #96
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            // necessary to initiate the oauth2 login flow
                            "/oauth2/authorization/**",
                            // we should not block the callback request from the auth provider
                            "/login/oauth2/code/**",
                            // allow unauthenticated access to error page, is used when dispayng erors in
                            // browser
                            "/error").permitAll();
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(ex -> ex
                        // replaces default 302 redirects with concrete 401 for unauthenicated users
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        }))
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler))
                .build();
    }
}