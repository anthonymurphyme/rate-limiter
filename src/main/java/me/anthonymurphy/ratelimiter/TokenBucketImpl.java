package me.anthonymurphy.ratelimiter;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;


public class TokenBucketImpl implements TokenBucket {

    private final long capacity;
    private final long period;
    private final TimeUnit timeUnit;
    private long availableTokens;
    private long lastRefillTimestamp;
    private long nextRefillTimestamp;
    private final Clock clock;

    TokenBucketImpl(Clock clock, long capacity, long period, TimeUnit unit){
        checkArgument(capacity > 0, "Token Bucket Capacity must be greater than 0");
        this.capacity = capacity;
        this.clock = clock;
        this.period = period;
        this.timeUnit = unit;

        this.availableTokens = capacity;
        this.lastRefillTimestamp = Instant.now(this.clock).toEpochMilli();
        this.nextRefillTimestamp = lastRefillTimestamp + (TimeUnit.MILLISECONDS.convert(this.period, this.timeUnit));
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

    /*
        Simple refill strategy, reset the bucket to capacity

     */
    @Override
    public synchronized void refill() {

        if (canRefillBucket()) {
            availableTokens = capacity;
            long refillTimestamp = Instant.now(this.clock).toEpochMilli();
            // increment lastRefillTimestamp to be consistent with the current time period rather than current time
            // for example if the period is an hour and refill is called 2 and 1/2 hours after the lest bucket refill,
            // then lastRefillTimestamp is set to lastRefillTimestamp + 2 hours
            //

            long numberOfPeriodsSinceLastRefill = Math.max(0, (refillTimestamp - this.lastRefillTimestamp)/TimeUnit.MILLISECONDS.convert(this.period, this.timeUnit));
            this.lastRefillTimestamp += numberOfPeriodsSinceLastRefill * TimeUnit.MILLISECONDS.convert(this.period, this.timeUnit);
            this.nextRefillTimestamp = lastRefillTimestamp + (TimeUnit.MILLISECONDS.convert(this.period, this.timeUnit));

        }
    }

    /*
        Checks if current time is great than nextRefillTime
     */

    private boolean canRefillBucket() {
        return Instant.now(clock).toEpochMilli() > getNextRefillTime();
    }

    /*
        Returns the timestamp for the next refill of bucket

        @return timestamp for next refill
     */
    @Override
    public long getNextRefillTime() {
        return nextRefillTimestamp;
    }

}
