package me.anthonymurphy.ratelimiter;

import static com.google.common.base.Preconditions.checkArgument;

public class TokenBucketImpl implements TokenBucket {

    private final long capacity;
    private final long availableTokens;

    TokenBucketImpl(long capacity){
        checkArgument(capacity > 0, "Token Bucket Capacity must be greater than 0");
        this.capacity = capacity;
        this.availableTokens = capacity;
    }

    /*
        Returns the capacity of the bucket, this is the maximum number of tokens that the bucket can hold at a time

        @return The capacity of the TokenBucket
     */

    @Override
    public long getCapacity() {
        return capacity;
    }

    /*
        Returns the current number of Tokens available in the bucket

        @return The current number of Tokens available in the bucket
     */
    @Override
    public long getAvailableTokens() {
        return availableTokens;
    }
}
