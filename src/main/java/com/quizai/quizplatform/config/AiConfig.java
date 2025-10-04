package com.quizai.quizplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AiConfig {

    @Bean
    WebClient ollamaClient(@Value("${ollama.host}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}
