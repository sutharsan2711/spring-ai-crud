package com.example.springaicrud.filter;

import com.fasterxml.jackson.databind
        .ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation
        .Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter
        .OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitFilter
        extends OncePerRequestFilter {

    private final Cache<String, Bucket>
            rateLimitCache;

    @Override
    protected void doFilterInternal(
            HttpServletRequest  request,
            HttpServletResponse response,
            FilterChain         chain)
            throws ServletException, IOException {

        // ✅ Get client IP address
        String clientIp = getClientIp(request);
        String requestUri = request.getRequestURI();

        // ✅ Create cache key
        // different buckets for auth vs general
        String cacheKey = isAuthRequest(requestUri)
                ? "auth:" + clientIp
                : "general:" + clientIp;

        // ✅ Get or create bucket for this IP
        Bucket bucket = rateLimitCache.get(
                cacheKey,
                key -> isAuthRequest(requestUri)
                        ? RateLimitConfig
                          .createAuthBucket()
                        : RateLimitConfig
                          .createGeneralBucket()
        );

        // ✅ Try to consume 1 token
        ConsumptionProbe probe =
                bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // ✅ Request allowed
            // Add rate limit headers
            response.addHeader(
                    "X-Rate-Limit-Remaining",
                    String.valueOf(
                            probe.getRemainingTokens()));
            response.addHeader(
                    "X-Rate-Limit-Retry-After",
                    "0");

            chain.doFilter(request, response);
        } else {
            // ❌ Too many requests
            long waitSeconds =
                    probe.getNanosToWaitForRefill()
                            / 1_000_000_000;

            log.warn("Rate limit exceeded for IP: {}"
                            + " on URL: {}",
                    clientIp, requestUri);

            // Send 429 Too Many Requests
            response.setStatus(
                    HttpStatus.TOO_MANY_REQUESTS
                            .value());
            response.setContentType(
                    MediaType.APPLICATION_JSON_VALUE);
            response.addHeader(
                    "X-Rate-Limit-Retry-After",
                    String.valueOf(waitSeconds));

            // Write error response
            Map<String, Object> error =
                    new LinkedHashMap<>();
            error.put("status", 429);
            error.put("error",
                    "Too Many Requests");
            error.put("message",
                    "Rate limit exceeded! "
                            + "Try again in "
                            + waitSeconds
                            + " seconds.");
            error.put("retryAfter",
                    waitSeconds + " seconds");
            error.put("timestamp",
                    new Date());

            new ObjectMapper()
                    .writeValue(
                            response.getWriter(),
                            error);
        }
    }

    // ✅ Check if request is auth endpoint
    private boolean isAuthRequest(String uri) {
        return uri != null
                && uri.startsWith("/api/auth");
    }

    // ✅ Get real client IP
    // handles proxies and load balancers
    private String getClientIp(
            HttpServletRequest request) {

        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip =
                    request.getHeader(header);
            if (ip != null
                    && !ip.isEmpty()
                    && !"unknown"
                    .equalsIgnoreCase(ip)) {
                // get first IP if multiple
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}