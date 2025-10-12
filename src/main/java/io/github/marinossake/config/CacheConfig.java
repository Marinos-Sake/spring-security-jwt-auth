package io.github.marinossake.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.marinossake.dto.UserReadOnlyDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, UserReadOnlyDTO> userProfileCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15)) // TTL 15 min
                .maximumSize(1000) //users
                .build();
    }
}