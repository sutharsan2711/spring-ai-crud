package com.example.springaicrud.filter;

import com.example.springaicrud.config
        .RateLimitConfig;   // ✅ Add this import
import com.fasterxml.jackson.databind
        .ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // ✅ Add this
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter
        .OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j                    // ✅ generates log
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

        String clientIp =
                getClientIp(request);
        String requestUri =
                request.getRequestURI();

        String cacheKey =
                isAuthRequest(requestUri)
                        ? "auth:" + clientIp
                        : "general:" + clientIp;

        // ✅ RateLimitConfig now resolved
        // because of import above
        Bucket bucket = rateLimitCache.get(
                cacheKey,
                key -> isAuthRequest(requestUri)
                        ? RateLimitConfig
                          .createAuthBucket()
                        : RateLimitConfig
                          .createGeneralBucket()
        );

        ConsumptionProbe probe =
                bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader(
                    "X-Rate-Limit-Remaining",
                    String.valueOf(
                            probe.getRemainingTokens()));
            chain.doFilter(request, response);
        } else {
            long waitSeconds =
                    probe.getNanosToWaitForRefill()
                            / 1_000_000_000;

            log.warn(
                    "Rate limit exceeded IP: {}",
                    clientIp);

            response.setStatus(429);
            response.setContentType(
                    MediaType.APPLICATION_JSON_VALUE);

            Map<String, Object> error =
                    new LinkedHashMap<>();
            error.put("status", 429);
            error.put("error",
                    "Too Many Requests");
            error.put("message",
                    "Rate limit exceeded! "
                            + "Try again in "
                            + waitSeconds + " seconds.");
            error.put("retryAfter",
                    waitSeconds + " seconds");
            error.put("timestamp", new Date());

            new ObjectMapper()
                    .writeValue(
                            response.getWriter(),
                            error);
        }
    }

    private boolean isAuthRequest(String uri) {
        return uri != null
                && uri.startsWith("/api/auth");
    }

    private String getClientIp(
            HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP"
        };
        for (String header : headers) {
            String ip =
                    request.getHeader(header);
            if (ip != null
                    && !ip.isEmpty()
                    && !"unknown"
                    .equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}