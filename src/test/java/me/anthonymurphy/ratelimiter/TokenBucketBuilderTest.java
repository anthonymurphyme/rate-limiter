package me.anthonymurphy.ratelimiter;

import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenBucketBuilderTest {

    @Test (expected = IllegalArgumentException.class)
    public void testTryCreateWithNoCapacity() {
        TokenBucketBuilder.builder()
                .withPeriod(10)
                .withTimeUnit(TimeUnit.MINUTES).build();
    }

    @Test (expected = IllegalArgumentException.class)
    public void testTryCreateWithNegativeCapacity() {
        TokenBucketBuilder.builder()
                .withCapacity(-1)
                .withPeriod(10)
                .withTimeUnit(TimeUnit.MINUTES).build();
    }

    @Test (expected = IllegalArgumentException.class)
    public void testTryCreateWithNoPeriod() {
        TokenBucketBuilder.builder()
                .withCapacity(10)
                .withTimeUnit(TimeUnit.MINUTES).build();
    }

    @Test (expected = IllegalArgumentException.class)
    public void testTryCreateWithNegativePeriod() {
        TokenBucketBuilder.builder()
                .withCapacity(10)
                .withPeriod(-1)
                .withTimeUnit(TimeUnit.MINUTES).build();
    }

    @Test (expected = NullPointerException.class)
    public void testTryCreateWithoutTimeUnit() {
        TokenBucketBuilder.builder()
                .withCapacity(10)
                .withPeriod(10).build();
    }

    @Test
    public void testCreate() {
        Instant periodStartTime = Instant.now();
        final Clock clock = mock(Clock.class);
        when(clock.instant()).thenAnswer(invocation -> periodStartTime);
        TokenBucket bucket = TokenBucketBuilder.builder()
                .withCapacity(10)
                .withPeriod(10)
                .withClock(clock)
                .withTimeUnit(TimeUnit.MINUTES)
                .build();
        assertEquals(10,bucket.getAvailableTokens());
        Instant nextRefillTime = periodStartTime.plus(10, ChronoUnit.MINUTES);
        assertEquals(nextRefillTime,bucket.getNextRefillTime());
    }

}
