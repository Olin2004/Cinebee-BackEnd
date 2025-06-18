package com.cinemazino.service;

public interface TokenBlacklistService {
    /**
     * Add a token to the blacklist with a specific TTL (time to live).
     * 
     * @param token     The JWT token to blacklist
     * @param ttlMillis Time to live in milliseconds
     */
    void blacklist(String token, long ttlMillis);

    /**
     * Check if a token is blacklisted.
     * 
     * @param token The JWT token to check
     * @return true if the token is blacklisted, false otherwise
     */
    boolean isBlacklisted(String token);
}
