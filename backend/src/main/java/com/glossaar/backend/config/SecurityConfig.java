package com.glossaar.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.glossaar.backend.auth.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

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
                // TODO: replace 302 redirects with concrete 401 for unauthenicated users at it
                // should be, issue: #90
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        // TODO: this should not be hardcoded, in live, it will be just /
                        .defaultSuccessUrl("http://localhost:5173/", true))
                // TODO: logout, #95
                .build();
    }
}