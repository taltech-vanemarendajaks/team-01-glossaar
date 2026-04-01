package com.glossaar.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.glossaar.backend.auth.OAuth2LoginSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Value("${app.post-login-landing-url}")
    private String postLoginLandingUrl;

    public SecurityConfig(OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO: Set up oauth2 login with server
        // TODO: replace JSESSIONID cookie with JWT token, #96
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/", "/login**", "/oauth2/**").permitAll();
                    auth.anyRequest().authenticated();

                })
                .exceptionHandling(ex -> ex
                        // replaces default 302 redirects with concrete 401 for unauthenicated users
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        }))
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .defaultSuccessUrl(postLoginLandingUrl, true))
                // TODO: logout, #95
                .build();
    }
}