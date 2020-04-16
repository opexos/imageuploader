package com.opexos.imageuploader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedisConfig {

    @Bean
    public ByteArrayRedisTemplate byteArrayRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new ByteArrayRedisTemplate(redisConnectionFactory);
    }

}
