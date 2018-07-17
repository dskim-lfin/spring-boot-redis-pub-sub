package com.teddy.example.api.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teddy.example.redis.model.CustomSubscriber;
import com.teddy.example.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class MainService {

    private static final String CHANNEL = "testChannel";

    private ConcurrentHashMap<String, String> cacheMap = new ConcurrentHashMap<>();
    private CustomSubscriber customSubscriber;
    private Thread localThread;
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .disableHtmlEscaping()
        .excludeFieldsWithModifiers(Modifier.TRANSIENT)
        .create();

    private RedisService redisService;

    @Autowired
    public MainService(RedisService redisService) {
        this.redisService = redisService;
    }

    private CustomSubscriber getCustomSubscriber() {
        if (customSubscriber == null) {
            customSubscriber = new CustomSubscriber() {
                @Override
                public void message(String channel, String message) {
                    update(message);
                }
            };
        }
        return customSubscriber;
    }

    @PostConstruct
    public void init() {
        if (localThread == null) {
            localThread = new Thread(() -> redisService.subscribe(CHANNEL, getCustomSubscriber()));
        }
        localThread.start();
    }

    @PreDestroy
    public void destroy() {
        redisService.unSubscribe(getCustomSubscriber());
    }

    private void update(String message) {
        try {
            String[] split = message.split("\\|");
            String key = split[0];
            String value = split[1];
            this.cacheMap.put(key, value);
            log.debug("update success : " + message);
        } catch (Exception e) {
            log.warn("update fail : " + message, e);
        }
    }

    public <T> Optional<T> get(String key, Class<T> tClass) {
        try {
            String valueString = this.cacheMap.get(key);
            return Optional.of(gson.fromJson(valueString, tClass));
        } catch (Exception e) {
            log.warn("get error > key " + key, e);
            return Optional.empty();
        }
    }

    public <T> T get(String key, Class<T> tClass, T defaultValue) {
        return get(key, tClass).orElse(defaultValue);
    }

    public Long publish(String key, String value) {
        String message = key + "|" + value;
        Long publish = redisService.publish(CHANNEL, message);
        return publish;
    }
}
