package com.rtb.bidder_service.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration
public class RedisCacheConfig {

    /**
     * This is the NEW 0.01% fix.
     *
     * Instead of just configuring the "campaigns" cache, we are now
     * creating the *default* configuration bean that Spring will
     * use for *all* cache operations.
     *
     * This ensures that *everything* (real campaign lists, empty lists,
     * nulls, etc.) is serialized to JSON, preventing the
     * "poisoned cache" problem from the default Java serializer.
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues() // Optional: Good practice
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    /**
     * This bean is no longer strictly necessary since we set the
     * default, but it's good practice to keep it. It explicitly
     * links our "campaigns" cache to the default config.
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        // This ensures the "campaigns" cache uses our new default config
        return (builder) -> builder
                .withCacheConfiguration("campaigns", cacheConfiguration());
    }
}