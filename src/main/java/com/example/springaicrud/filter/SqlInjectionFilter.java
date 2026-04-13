package com.example.springaicrud.filter;

import com.fasterxml.jackson.databind
        .ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
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
import java.util.regex.Pattern;

@Slf4j
@Component
@Order(2)
public class SqlInjectionFilter
        extends OncePerRequestFilter {

    // ✅ SQL injection patterns to block
    private static final List<Pattern>
            SQL_PATTERNS = Arrays.asList(

            // Basic SQL injection
            Pattern.compile(
                    "('.+--)|(--)|(\\|)|" +
                            "(\\*)|(%27)|(%22)",
                    Pattern.CASE_INSENSITIVE),

            // SQL keywords
            Pattern.compile(
                    "\\b(SELECT|INSERT|UPDATE|DELETE|" +
                            "DROP|CREATE|ALTER|EXEC|UNION|" +
                            "TRUNCATE|DECLARE|CAST|CONVERT|" +
                            "CHAR|NCHAR|VARCHAR|NVARCHAR|" +
                            "HAVING|WAITFOR|XP_)\\b",
                    Pattern.CASE_INSENSITIVE),

            // SQL comments
            Pattern.compile(
                    "(--[\\s\\S]*$)|(/\\*[\\s\\S]*?\\*/)",
                    Pattern.CASE_INSENSITIVE),

            // Script injection (XSS)
            Pattern.compile(
                    "<script[^>]*>[\\s\\S]*?</script>",
                    Pattern.CASE_INSENSITIVE),

            // HTML injection
            Pattern.compile(
                    "<[^>]+>",
                    Pattern.CASE_INSENSITIVE),

            // OR/AND injection
            Pattern.compile(
                    "\\b(OR|AND)\\b.+=.+",
                    Pattern.CASE_INSENSITIVE)
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest  request,
            HttpServletResponse response,
            FilterChain         chain)
            throws ServletException, IOException {

        // ✅ Check URL parameters
        String queryString =
                request.getQueryString();
        if (queryString != null
                && isMalicious(queryString)) {
            sendBlockedResponse(
                    response,
                    request.getRemoteAddr(),
                    "URL parameter",
                    queryString);
            return;
        }

        // ✅ Check path variables
        String requestUri =
                request.getRequestURI();
        if (isMalicious(requestUri)) {
            sendBlockedResponse(
                    response,
                    request.getRemoteAddr(),
                    "URL path",
                    requestUri);
            return;
        }

        // ✅ Check all request parameters
        Enumeration<String> paramNames =
                request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName =
                    paramNames.nextElement();
            String paramValue =
                    request.getParameter(paramName);

            if (paramValue != null
                    && isMalicious(paramValue)) {
                sendBlockedResponse(
                        response,
                        request.getRemoteAddr(),
                        "parameter: " + paramName,
                        paramValue);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    // ✅ Check if input contains
    // malicious patterns
    private boolean isMalicious(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        for (Pattern pattern : SQL_PATTERNS) {
            if (pattern.matcher(input).find()) {
                return true;
            }
        }

        return false;
    }

    // ✅ Send 400 blocked response
    private void sendBlockedResponse(
            HttpServletResponse response,
            String clientIp,
            String location,
            String value)
            throws IOException {

        log.warn("SQL Injection attempt blocked!"
                        + " IP: {} Location: {} Value: {}",
                clientIp, location,
                value.length() > 50
                        ? value.substring(0, 50)
                          + "..."
                        : value);

        response.setStatus(
                HttpStatus.BAD_REQUEST.value());
        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> error =
                new LinkedHashMap<>();
        error.put("status", 400);
        error.put("error", "Bad Request");
        error.put("message",
                "Invalid input detected! "
                        + "Request blocked for security.");
        error.put("timestamp", new Date());

        new ObjectMapper()
                .writeValue(
                        response.getWriter(), error);
    }

    // ✅ Skip filter for specific URLs
    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {
        String path = request.getRequestURI();
        // skip for image uploads
        return path.contains("/image");
    }
}