package com.opexos.imageuploader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemcachedConfig {

    @Bean
    public MemcachedClient memcachedClient(@Value("${memcached.address}") String address,
                                           @Value("${memcached.connection-pool-size}") int connectionPoolSize,
                                           @Value("${memcached.timeout}") long timeout) {
        return new MemcachedClient(address, connectionPoolSize, timeout);
    }

}
