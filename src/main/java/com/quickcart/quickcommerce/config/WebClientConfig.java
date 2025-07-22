package com.quickcart.quickcommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Bean
    public WebClient geminiWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                // Base URL up to the models directory
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/") // <-- CHANGED THIS LINE
                .defaultHeader("x-goog-api-key", geminiApiKey)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}