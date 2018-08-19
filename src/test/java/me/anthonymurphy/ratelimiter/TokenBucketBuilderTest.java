package me.anthonymurphy.ratelimiter;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

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

}
