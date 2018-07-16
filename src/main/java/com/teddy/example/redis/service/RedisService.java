package com.teddy.example.redis.service;

import com.teddy.example.redis.model.CustomSubscriber;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

@Service
@Slf4j
public class RedisService {

    private RedisClient redisClient;

    @Autowired
    public RedisService(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public Mono<String> get(String key) {
        RedisAsyncCommands<String, String> async = this.redisClient.connect().async();
        return Mono.create((MonoSink<String> sink) -> async.get(key).thenAcceptAsync(sink::success));
    }

    public Mono<String> set(String key, String value) {
        return this.set(key, value, -1L);
    }

    public Mono<String> set(String key, String value, Long expire) {
        RedisAsyncCommands<String, String> async = this.redisClient.connect().async();
        return Mono.create(sink -> async.set(key, value, SetArgs.Builder.ex(expire)).thenAcceptAsync(sink::success));
    }

    public void subscribe(String channel, CustomSubscriber subscriber) {
        StatefulRedisPubSubConnection<String, String> connection = this.redisClient.connectPubSub();
        connection.addListener(subscriber);

        RedisPubSubCommands<String, String> sync = connection.sync();
        sync.subscribe(channel);
    }

    public void unSubscribe(CustomSubscriber subscriber) {
        this.redisClient.connectPubSub().removeListener(subscriber);
    }

    public Long publish(String channel, String message) {
        RedisPubSubCommands<String, String> sync = this.redisClient.connectPubSub().sync();
        Long publish = sync.publish(channel, message);
        return publish;
    }
}
