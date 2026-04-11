package com.glossaar.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import com.glossaar.backend.auth.OAuthLoginSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Whitelist swagger endpoints, swagger is only run locally as if now.
    private static final String[] SWAGGER_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
    };

    // OAuth2 OIDC flow endpoints
    private static final String[] AUTH_URLS = {
            // necessary to initiate the oauth2 login flow from FE side
            "/oauth2/authorization/**",
            // we should not block the callback request from the auth provider
            "/login/oauth2/code/**",
    };

    private final OAuthLoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(OAuthLoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO: replace JSESSIONID cookie with JWT token, #96
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(AUTH_URLS).permitAll();
                    auth.requestMatchers(SWAGGER_URLS).permitAll();
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
