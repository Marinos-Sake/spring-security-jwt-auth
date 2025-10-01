package com.quizai.quizplatform.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.quizai.quizplatform.dto.UserReadOnlyDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, UserDetails> userDetailsCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15)) // TTL 15 min
                .maximumSize(1000)  //users
                .build();
    }

    @Bean
    public Cache<String, UserReadOnlyDTO> userProfileCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15))
                .maximumSize(1000)
                .build();
    }
}