package me.anthonymurphy.ratelimiter;

import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenBucketImplTest {

    private static final long CAPACITY = 60;
    private static final long PERIOD = 1;
    private static final TimeUnit TIME_UNIT_HOURS = TimeUnit.HOURS;
    private Instant currentTime;
    private TokenBucketImpl bucket;

    @Before
    public void setUp() {
        currentTime = Instant.now();
        final Clock clock = mock(Clock.class);
        when(clock.instant()).thenAnswer((invocation) -> currentTime);
        bucket = new TokenBucketImpl(clock, CAPACITY, PERIOD , TIME_UNIT_HOURS);
    }


    @Test
    public void testGetCapacity() {

        assertEquals(CAPACITY,bucket.getCapacity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCapacityCreation() {
        new TokenBucketImpl(Clock.systemUTC(), -1, PERIOD , TIME_UNIT_HOURS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroCapacityCreate() {
        new TokenBucketImpl(Clock.systemUTC(),0, PERIOD , TIME_UNIT_HOURS);
    }

    @Test
    public void testAvailableTokensAfterCreation() {
        assertEquals(CAPACITY,bucket.getAvailableTokens());
    }
    @Test
    public void testConsumingTokenDecreasesAvailableTokens() {
        assertTrue(bucket.consume());
        assertEquals(CAPACITY-1,bucket.getAvailableTokens());
    }

    @Test
    public void testConsuminTwoTokensDecreasesAvailableTokens() {
        assertTrue(bucket.consume());
        assertTrue(bucket.consume());
        assertEquals(CAPACITY-2,bucket.getAvailableTokens());
    }

    @Test
    public void testConsumingLastRemainingTokenDecreasesAvailableTokensToZero() {
        bucket = new TokenBucketImpl(Clock.systemUTC(), 1, PERIOD , TIME_UNIT_HOURS);
        assertTrue(bucket.consume());
        assertEquals(0,bucket.getAvailableTokens());
    }

    @Test
    public void testConsumingTokenWhenNoAvailableTokens() {
        bucket = new TokenBucketImpl(Clock.systemUTC(), 1, PERIOD , TIME_UNIT_HOURS);
        assertTrue(bucket.consume());
        assertFalse(bucket.consume());
        assertEquals(0,bucket.getAvailableTokens());
    }

    @Test
    public void testRefillResetsBucketToCapacity() {
        assertTrue(bucket.consume());
        bucket.refill();
        assertEquals(CAPACITY, bucket.getAvailableTokens());
    }

    @Test
    public void testRefillAtCapacity() {
        bucket.refill();
        assertEquals(CAPACITY, bucket.getAvailableTokens());
    }

    @Test
    public void testAfterCreationNextRefillIsOneHour() {
        long oneHour = 60 * 60 * 1000;
        long nextRefillTime = currentTime.toEpochMilli() + oneHour;
        assertEquals(nextRefillTime,bucket.getNextRefillTime());
    }


}
