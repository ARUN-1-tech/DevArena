package com.devarena.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis configuration providing connection pooling, serialization templates,
 * and key namespace standards for DevArena caching and real-time state.
 */
@Configuration
public class RedisConfig {

    // Standard Redis Key Namespace Prefix Constants
    public static final String KEY_PREFIX_MATCHMAKING_QUEUE = "devarena:matchmaking:queue";
    public static final String KEY_PREFIX_BATTLE_STATE = "devarena:battle:state:";
    public static final String KEY_PREFIX_PLAYER_PRESENCE = "devarena:presence:";
    public static final String KEY_PREFIX_LEADERBOARD = "devarena:leaderboard:global";
    public static final String KEY_PREFIX_RATE_LIMIT = "devarena:ratelimit:";

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
