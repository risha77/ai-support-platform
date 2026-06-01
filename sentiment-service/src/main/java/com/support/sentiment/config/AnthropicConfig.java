package com.support.sentiment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AnthropicConfig {

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.base-url:https://api.anthropic.com}")
    private String baseUrl;

    @Bean
    public WebClient anthropicClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
