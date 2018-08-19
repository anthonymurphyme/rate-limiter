package me.anthonymurphy.ratelimiter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TokenBucketImplTest {

    private static final long CAPACITY = 60;

    @Test
    public void testGetCapacity() {
        TokenBucketImpl bucket = new TokenBucketImpl(CAPACITY);
        assertEquals(CAPACITY,bucket.getCapacity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCapacityCreation() {
        new TokenBucketImpl(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroCapacityCreate() {
        new TokenBucketImpl(0);
    }

    @Test
    public void testAvailableTokensAfterCreation() {
        TokenBucketImpl bucket = new TokenBucketImpl(CAPACITY);
        assertEquals(CAPACITY,bucket.getAvailableTokens());
    }
    @Test
    public void testConsumingTokenDecreasesAvailableTokens() {
        TokenBucketImpl bucket = new TokenBucketImpl(CAPACITY);
        assertTrue(bucket.consume());
        assertEquals(CAPACITY-1,bucket.getAvailableTokens());
    }

    @Test
    public void testConsumingLastRemainingTokenDecreasesAvailableTokensToZero() {
        TokenBucketImpl bucket = new TokenBucketImpl(1);
        assertTrue(bucket.consume());
        assertEquals(0,bucket.getAvailableTokens());
    }

    @Test
    public void testConsumingTokenWhenNoAvailableTokens() {
        TokenBucketImpl bucket = new TokenBucketImpl(1);
        assertTrue(bucket.consume());
        assertFalse(bucket.consume());
        assertEquals(0,bucket.getAvailableTokens());
    }

    @Test
    public void testRefillResetsBucketToCapacity() {
        TokenBucketImpl bucket = new TokenBucketImpl(CAPACITY);
        assertTrue(bucket.consume());
        bucket.refill();
        assertEquals(CAPACITY, bucket.getAvailableTokens());
    }

    @Test
    public void testRefillAtCapacity() {
        TokenBucketImpl bucket = new TokenBucketImpl(CAPACITY);
        bucket.refill();
        assertEquals(CAPACITY, bucket.getAvailableTokens());
    }


}
