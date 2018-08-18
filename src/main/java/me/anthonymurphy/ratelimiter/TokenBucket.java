package me.anthonymurphy.ratelimiter;


/**
 * Using a Token Bucket algorithm to limit access to resources
 *
 * @see <a href="http://en.wikipedia.org/wiki/Token_bucket">Token Bucket </a>
 *
 */

public interface TokenBucket {


    long getCapacity();

    long getAvailableTokens();


}
