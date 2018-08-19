package me.anthonymurphy.ratelimiter;

import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RateLimiterImpTest {

    private static final long CAPACITY = 3;
    private static final long PERIOD = 1;
    private static final TimeUnit TIME_UNIT_HOURS = TimeUnit.HOURS;
    private Instant periodStartTimeClient1;
    private TokenBucket tokenBucketClient1;
    private String clientId1 = "127.0.0.1";
    private Instant periodStartTimeClient2;
    private TokenBucket tokenBucketClient2;
    private String clientId2 = "192.168.01";
    private RateLimiter rateLimiter;

    @Before
    public void setUp() {
        periodStartTimeClient1 = Instant.now();
        final Clock clockClient1 = mock(Clock.class);
        when(clockClient1.instant()).thenAnswer(invocation -> periodStartTimeClient1);
        tokenBucketClient1 = new TokenBucketImpl(clockClient1, CAPACITY, PERIOD , TIME_UNIT_HOURS);
        periodStartTimeClient2 = Instant.now().plus(5, ChronoUnit.MINUTES);
        final Clock clockClient2 = mock(Clock.class);
        when(clockClient2.instant()).thenAnswer(invocation -> periodStartTimeClient2);
        tokenBucketClient2 = new TokenBucketImpl(clockClient2, CAPACITY, PERIOD , TIME_UNIT_HOURS);
        rateLimiter = new RateLimiterImpl(CAPACITY, PERIOD , TIME_UNIT_HOURS);
    }

    @Test
    public void testAllowRequestSimpleScenario(){
        TokenBucket bucket = new TokenBucketImpl(Clock.systemUTC(), 1, PERIOD , TIME_UNIT_HOURS);
        rateLimiter.addClient(clientId1, bucket);
        assertTrue(rateLimiter.allowRequest(clientId1));
        assertFalse(rateLimiter.allowRequest(clientId1));
    }

    @Test
    public void testAllowRequestSimpleScenarioWithoutManualAdd(){
        RateLimiter rateLimiterWithCapacityOf1 = new RateLimiterImpl(1, PERIOD , TIME_UNIT_HOURS);
        assertTrue(rateLimiterWithCapacityOf1.allowRequest(clientId1));
        assertFalse(rateLimiterWithCapacityOf1.allowRequest(clientId1));
    }


    @Test
    public void testAllowMultipleRequestWithinTimePeriod() {
        rateLimiter.addClient(clientId1, tokenBucketClient1);
        assertTrue(rateLimiter.allowRequest(clientId1));
        assertTrue(rateLimiter.allowRequest(clientId1));
        assertTrue(rateLimiter.allowRequest(clientId1));
    }

    @Test
    public void testAllowRequestAfterDepletionAndRefill() {
        rateLimiter.addClient(clientId1, tokenBucketClient1);
        assertTrue(rateLimiter.allowRequest(clientId1));
        assertTrue(rateLimiter.allowRequest(clientId1));
        assertTrue(rateLimiter.allowRequest(clientId1));
        assertFalse(rateLimiter.allowRequest(clientId1));

        // Move forward 61 minutes and the bucket should refill
        periodStartTimeClient1 = periodStartTimeClient1.plus(61, ChronoUnit.MINUTES);
        assertTrue(rateLimiter.allowRequest(clientId1));

    }

    @Test
    public void testAllowRequestSimpleScenarioMultipleClients(){
        TokenBucket bucketClient1 = new TokenBucketImpl(Clock.systemUTC(), 1, PERIOD , TIME_UNIT_HOURS);
        TokenBucket bucketClient2 = new TokenBucketImpl(Clock.systemUTC(), 1, PERIOD , TIME_UNIT_HOURS);

        rateLimiter.addClient(clientId1, bucketClient1);
        rateLimiter.addClient(clientId2, bucketClient2);

        assertTrue(rateLimiter.allowRequest(clientId1));
        assertFalse(rateLimiter.allowRequest(clientId1));

        assertTrue(rateLimiter.allowRequest(clientId2));
        assertFalse(rateLimiter.allowRequest(clientId2));
    }

    @Test
    public void testAllowRequestSimpleScenarioMultipleClientsWithoutManualAdd(){
        RateLimiter rateLimiterWithCapacityOf1 = new RateLimiterImpl(1, PERIOD , TIME_UNIT_HOURS);

        assertTrue(rateLimiterWithCapacityOf1.allowRequest(clientId1));
        assertFalse(rateLimiterWithCapacityOf1.allowRequest(clientId1));

        assertTrue(rateLimiterWithCapacityOf1.allowRequest(clientId2));
        assertFalse(rateLimiterWithCapacityOf1.allowRequest(clientId2));
    }

    @Test
    public void testAllowRequestAfterDepletionAndRefillMultipleClients() {
        rateLimiter.addClient(clientId1, tokenBucketClient1);
        rateLimiter.addClient(clientId2, tokenBucketClient2);
        assertTrue(rateLimiter.allowRequest(clientId1));
        assertTrue(rateLimiter.allowRequest(clientId1));
        assertTrue(rateLimiter.allowRequest(clientId1));
        assertFalse(rateLimiter.allowRequest(clientId1));

        assertTrue(rateLimiter.allowRequest(clientId2));
        assertTrue(rateLimiter.allowRequest(clientId2));
        assertTrue(rateLimiter.allowRequest(clientId2));
        assertFalse(rateLimiter.allowRequest(clientId2));
        assertFalse(rateLimiter.allowRequest(clientId2));

        // Move Client1 forward 61 minutes and the bucket should refill
        periodStartTimeClient1 = periodStartTimeClient1.plus(61, ChronoUnit.MINUTES);
        // Move Client2 forward 56 minutes, it is now at the same time as Client 1 but bucket should not be refilled
        periodStartTimeClient2 = periodStartTimeClient2.plus(56, ChronoUnit.MINUTES);

        assertTrue(rateLimiter.allowRequest(clientId1));
        // As Client2 started 5 minutes after client1

        assertFalse("Client 2 has consumed a token", rateLimiter.allowRequest(clientId2));

        // Move Client2 forward 5 mins and the bucket should refill
        periodStartTimeClient2 = periodStartTimeClient2.plus(5, ChronoUnit.MINUTES);

        assertTrue(rateLimiter.allowRequest(clientId2));
    }
}
