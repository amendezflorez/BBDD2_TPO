package com.findra.config;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

@Configuration
public class CacheConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        try {
            redisConnectionFactory.getConnection().ping();
            RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofSeconds(30));
            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(cacheConfiguration)
                    .build();
        } catch (Exception exception) {
            log.warn("Redis no disponible; usando caché en memoria temporal", exception);
            return new ConcurrentMapCacheManager("dashboard-resumen");
        }
    }
}