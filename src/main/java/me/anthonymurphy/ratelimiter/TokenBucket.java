package me.anthonymurphy.ratelimiter;


import java.time.Instant;

/**
 * Using a Token Bucket algorithm to limit access to resources
 *
 * This Token Bucket follows a traffic policing strategy, that is if the bucket is empty the request to consume a token
 * will fail rather than block on available token
 *
 * @see <a href="http://en.wikipedia.org/wiki/Token_bucket">Token Bucket </a>
 *
 */

public interface TokenBucket {


    long getCapacity();
    long getAvailableTokens();
    boolean consume();
    void refill();
    Instant getNextRefillTime();


}
