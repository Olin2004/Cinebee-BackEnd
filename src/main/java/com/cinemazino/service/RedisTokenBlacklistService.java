package com.cinemazino.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisTokenBlacklistService implements TokenBlacklistService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void blacklist(String token, long ttlMillis) {
        String key = "blacklist:" + token;
        redisTemplate.opsForValue().set(key, "true", ttlMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isBlacklisted(String token) {
        String key = "blacklist:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
