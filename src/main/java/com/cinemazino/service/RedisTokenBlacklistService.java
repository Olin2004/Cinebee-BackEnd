package com.cinemazino.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisTokenBlacklistService implements TokenBlacklistService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Add a token to the blacklist in Redis with a specific TTL (time to live).
     * 
     * @param token     The JWT token to blacklist
     * @param ttlMillis Time to live in milliseconds
     */
    @Override
    public void blacklist(String token, long ttlMillis) {
        String key = "blacklist:" + token;
        redisTemplate.opsForValue().set(key, "true", ttlMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Check if a token is blacklisted in Redis.
     * 
     * @param token The JWT token to check
     * @return true if the token is blacklisted, false otherwise
     */
    @Override
    public boolean isBlacklisted(String token) {
        String key = "blacklist:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
