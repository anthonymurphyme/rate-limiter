package me.anthonymurphy.ratelimiter;


/**
 * Ratelimiter allows access to a resource if rate limiting strategy allows
 *
 *
 */

public interface RateLimiter {

    boolean allowRequest(String requestorId);
    void addClient(String clientId, TokenBucket tokenBucket);

}
