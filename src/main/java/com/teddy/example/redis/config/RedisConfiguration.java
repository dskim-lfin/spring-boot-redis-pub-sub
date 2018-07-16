package com.teddy.example.redis.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spring.redis")
@Setter
@Configuration
@Slf4j
public class RedisConfiguration {

    private String host;
    private Integer port;

    @Bean
    public RedisClient redisClient() {
        return RedisClient.create(RedisURI.Builder.redis(host, port).build());
    }
}
