package me.anthonymurphy.ratelimiter;

import static com.google.common.base.Preconditions.checkArgument;

public class TokenBucketImpl implements TokenBucket {

    private final long capacity;
    private long availableTokens;

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

    /*
        Returns true if successfully consumed a token, false if no tokens available

        @return {@code true} if token was consumed, {@code false} otherwise
     */

    @Override
    public boolean consume() {

        if (availableTokens >= 1) {
            availableTokens -= 1;
            return true;
        }

        return false;
    }
}
