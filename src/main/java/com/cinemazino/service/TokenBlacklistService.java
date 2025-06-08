package com.cinemazino.service;

public interface TokenBlacklistService {
    void blacklist(String token, long ttlMillis);

    boolean isBlacklisted(String token);
}
