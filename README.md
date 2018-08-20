# Introduction [![Build Status](https://travis-ci.org/anthonymurphyme/rate-limiter.svg?branch=master)](https://travis-ci.org/anthonymurphyme/rate-limiter)
This module provides a simple rate-limiting library that stops a particular client from making too many requests within a particular period of time. It uses:
* A Simple Token Bucket algorithm to allocate access tokens to a client. On request from a client the RateLimiter tries to consume a token from the bucket, if successful access may be granted otherwise access should be denied. In this simple case, tokens are replenished to capicity on the first request after current time period has elasped.
* A local in memory cache to store clientID and the associated TokenBucket.

For more details on the Token Bucket algorithm see
* [Wikipedia - Token Bucket](http://en.wikipedia.org/wiki/Token_bucket)

## Usage
Create a Rate Limiter which will allow clients to access a resource at a rate of 60 request per hour.
```java
   RateLimiter rateLimiter = new RateLimiterImpl(60, 1 , TimeUnit.HOURS);

// ...

  // When a Client requests access to a resource, allowRequest will try to consume a token
  // from the bucket, if it is successful, access can be granted otherwise access
  // should be denied.
  if (rateLimiter.allowRequest("fb2e77d.47a0479900504cb3ab4a1f626d174d2d") {
     allowAccess();
  } else {
     denyAccess();
     // For example return a HTTP Error with status code 429 - Too Many Requests
  }
```

## Getting Started
To build and manage dependencies install Gradle.
See [Gradle Install Instructions](https://gradle.org/install/)

## Built With
* [Guava](https://github.com/google/guava) - Google core libraries for Java
* [Gradle](https://gradle.org/) - Dependency Management
