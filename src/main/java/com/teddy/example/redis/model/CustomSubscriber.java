package com.teddy.example.redis.model;

import io.lettuce.core.pubsub.RedisPubSubListener;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CustomSubscriber implements RedisPubSubListener<String, String> {

    @Override
    public void message(String channel, String message) {
        log.info("message > channel : " + channel + " , message : " + message);
    }

    @Override
    public void message(String pattern, String channel, String message) {
        log.info("message > pattern : " +pattern + ", channel : " + channel + " , message : " + message);
    }

    @Override
    public void subscribed(String channel, long count) {
        log.info("subscribed > channel : " + channel + " , count : " + count);
    }

    @Override
    public void psubscribed(String pattern, long count) {
        log.info("psubscribed > pattern : " + pattern + " , count : " + count);
    }

    @Override
    public void unsubscribed(String channel, long count) {
        log.info("unsubscribed > channel : " + channel + " , count : " + count);
    }

    @Override
    public void punsubscribed(String pattern, long count) {
        log.info("punsubscribed > pattern : " + pattern + " , count : " + count);
    }
}
