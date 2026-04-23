package com.glossaar.backend.wordnik;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class WordnikConfig {

    @Bean
    public RestClient wordnikRestClient(@Value("${wordnik.api.base-url}") String baseUrl,
                                    @Value("${wordnik.api.key}") String apiKey) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(
            HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build()
        );
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        return RestClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("api_key", apiKey)
            .requestFactory(requestFactory)
            .build();
    }
}
