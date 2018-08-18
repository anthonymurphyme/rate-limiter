package me.anthonymurphy.ratelimiter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

}
