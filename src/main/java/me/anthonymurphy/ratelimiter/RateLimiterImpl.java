package me.anthonymurphy.ratelimiter;


/*

 */


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

public class RateLimiterImpl implements RateLimiter {

    private LoadingCache<String, TokenBucket> clientCache;

    RateLimiterImpl(long capacity, long period, TimeUnit timeUnit) {

        clientCache = CacheBuilder.newBuilder()
               .build(
                       new CacheLoader<String, TokenBucket>() {
                           public TokenBucket load(String id) {
                               return TokenBucketBuilder.builder()
                                       .withCapacity(capacity)
                                       .withPeriod(period)
                                       .withTimeUnit(timeUnit)
                                       .build();
                           }
                       }
               );
    }

    /*
        Checks if the calling client can access the resource by consuming a token from the TokenBucket associated with
        the Client

        An entry in the cache is populated with a TokenBucket if this is first request from the Client

        @param clientId unique identifier for the Client, for example Session ID, IP Address

        @return {@code true} if a token was consumed and Client may access the resource, {@code false} otherwise and
        the Client may not access the resource
     */
    @Override
    public boolean allowRequest(String clientId) {
        return clientCache.getUnchecked(clientId).consume();
    }

    /*
        Add a specific TokenBucket for a Client to the cache, which allows the caller to specify a per client rate
        limiting strategy. Will overwrite any existing entries for the Client in the cache.

        @param clientId unique identifier for the Client, for example Session ID, IP Address
        @param tokenBucket previously constructed TokenBucket
     */
    @Override
    public void addClient(String clientId, TokenBucket tokenBucket){
        clientCache.put(clientId, tokenBucket);
    }
}
