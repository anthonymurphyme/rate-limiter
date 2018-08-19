package me.anthonymurphy.ratelimiter;

import java.time.Clock;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


public final class TokenBucketBuilder {

    private TokenBucketBuilder() { }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long capacity = 0;
        private long period = 0;
        private TimeUnit timeUnit = null;
        private Clock clock = null;

        public Builder withCapacity(long capacity)  {
            checkArgument(capacity > 0, "Token Bucket Capacity must be greater than 0");
            this.capacity = capacity;
            return this;
        }

        public Builder withPeriod(long period) {
            checkArgument(period > 0, "Period must be greater than 0");
            this.period = period;
            return this;
        }

        public Builder withTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public TokenBucket build() {
            checkNotNull(this.timeUnit, "TimeUnit must be specified");
            if (clock == null)
                clock = Clock.systemUTC();
            return new TokenBucketImpl(clock, this.capacity, this.period, this.timeUnit);
        }


    }


}
