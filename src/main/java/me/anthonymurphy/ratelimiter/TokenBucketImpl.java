package me.anthonymurphy.ratelimiter;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


public class TokenBucketImpl implements TokenBucket {

    private final long capacity;
    private final long period;
    private final TimeUnit timeUnit;
    private long availableTokens;
    private Instant startOfRefillPeriod;
    private Instant nextRefillTime;
    private final Clock clock;

    TokenBucketImpl(Clock clock, long capacity, long period, TimeUnit unit){
        checkArgument(capacity > 0, "Token Bucket Capacity must be greater than 0");
        checkArgument(period > 0, "Period must be greater than 0");
        checkNotNull(unit, "TimeUnit must be set");
        this.capacity = capacity;
        this.clock = clock;
        this.period = period;
        this.timeUnit = unit;

        this.availableTokens = capacity;
        this.startOfRefillPeriod = Instant.now(this.clock);
        this.nextRefillTime = startOfRefillPeriod.plusMillis(getPeriodInMilliseconds());
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

        refill();

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
            Instant currentRefillTime = Instant.now(this.clock);

            // increment startOfRefillPeriod to be consistent with the current time period rather than current time
            // for example if the period is an hour and refill is called 2 and 1/2 hours after the lest bucket refill,
            // then startOfRefillPeriod is set to startOfRefillPeriod + 2 hours

            long numberOfPeriodsSinceLastRefill = Math.max(0, getPeriodsSinceLastRefill(currentRefillTime));
            this.startOfRefillPeriod = this.startOfRefillPeriod.plusMillis(numberOfPeriodsSinceLastRefill * getPeriodInMilliseconds());

            this.nextRefillTime = startOfRefillPeriod.plusMillis(getPeriodInMilliseconds());

        }
    }


    /*
         Calculates number of periods since last bucket refill

         @param RefillTime current refill time
         @return Number of periods since last refill
     */

    private long getPeriodsSinceLastRefill(Instant refillTime) {
        return getMillisecondsSinceLastRefill(refillTime) / getPeriodInMilliseconds();
    }


    private long getMillisecondsSinceLastRefill(Instant refillTime) {
        return (refillTime.getEpochSecond() - startOfRefillPeriod.getEpochSecond()) * 1000;
    }

    /*
        Calculates the refill period in milliseconds

        @return The number of milliseconds in the refill period
     */

    private long getPeriodInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(this.period, this.timeUnit);
    }

    /*
        Checks if current time is great than nextRefillTime
     */

    private boolean canRefillBucket() {
        return Instant.now(clock).compareTo(getNextRefillTime()) > 0;
    }

    /*
        Returns the timestamp for the next refill of bucket

        @return timestamp for next refill
     */
    @Override
    public Instant getNextRefillTime() {
        return nextRefillTime;
    }

}
