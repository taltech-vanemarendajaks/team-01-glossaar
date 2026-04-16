package com.glossaar.backend.config;

import com.glossaar.backend.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.glossaar.backend.auth.AuthLogoutSuccessHandler;
import com.glossaar.backend.auth.OAuthLoginSuccessHandler;
import com.glossaar.backend.auth.jwt.JwtAuthenticationFilter;
import com.glossaar.backend.auth.jwt.JwtService;

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
    private final AuthLogoutSuccessHandler authLogoutSuccessHandler;
    private final JwtService jwtService;

    public SecurityConfig(OAuthLoginSuccessHandler oAuth2LoginSuccessHandler, AuthLogoutSuccessHandler authLogoutSuccessHandler, JwtService jwtService) {
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.authLogoutSuccessHandler = authLogoutSuccessHandler;
        this.jwtService = jwtService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, userService);

        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(AUTH_URLS).permitAll();
                auth.requestMatchers(SWAGGER_URLS).permitAll();
                auth.anyRequest().authenticated();
            })
            .logout(logout -> logout
                .logoutSuccessHandler(authLogoutSuccessHandler))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                                              response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                ))
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2LoginSuccessHandler))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
