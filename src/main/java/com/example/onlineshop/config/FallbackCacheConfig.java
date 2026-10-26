package com.example.onlineshop.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "redis.enabled", havingValue = "false", matchIfMissing = true)
public class FallbackCacheConfig {
    @Bean
    public CacheManager fallbackCacheManager(
            @Value("${app.cache.fallback.max-entries:1000}") long maxEntries,
            @Value("${app.cache.fallback.ttl-minutes:10}") long ttlMinutes) {
        if (maxEntries < 1 || ttlMinutes < 1) {
            throw new IllegalArgumentException("Fallback cache bounds must be positive");
        }
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(maxEntries)
                .expireAfterWrite(Duration.ofMinutes(ttlMinutes))
                .recordStats());
        manager.setAllowNullValues(false);
        return manager;
    }
}
