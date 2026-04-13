package com.example.springaicrud.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation
        .Bean;
import org.springframework.context.annotation
        .Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@SuppressWarnings("deprecation")
public class RateLimitConfig {

    // ✅ Cache to store rate limit buckets
    // per IP address
    @Bean
    public Cache<String, Bucket>
    rateLimitCache() {
        return Caffeine.newBuilder()
                // remove after 1 hour inactivity
                .expireAfterAccess(
                        1, TimeUnit.HOURS)
                // max 10000 IP addresses
                .maximumSize(10000)
                .build();
    }

    // ✅ General API bucket
    // 60 requests per minute
    public static Bucket createGeneralBucket() {
        Bandwidth limit = Bandwidth
                .classic(60,
                        Refill.intervally(
                                60,
                                Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    // ✅ Auth API bucket (stricter)
    // 5 requests per minute
    public static Bucket createAuthBucket() {
        Bandwidth limit = Bandwidth
                .classic(5,
                        Refill.intervally(
                                5,
                                Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    // ✅ Strict bucket for sensitive APIs
    // 10 requests per minute
    public static Bucket createStrictBucket() {
        Bandwidth limit = Bandwidth
                .classic(10,
                        Refill.intervally(
                                10,
                                Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}