package com.example.springaicrud.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory
        .annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter
        .OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService
            userDetailsService;

    @Autowired
    public JwtFilter(
            JwtUtil jwtUtil,
            @Lazy UserDetailsService
                    userDetailsService) {
        this.jwtUtil            = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest  request,
            HttpServletResponse response,
            FilterChain         chain)
            throws ServletException, IOException {

        String header = request
                .getHeader("Authorization");
        String token  = null;
        String email  = null;

        // extract token from Bearer header
        if (header != null
                && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                email = jwtUtil.extractEmail(token);
            } catch (Exception ignored) {}
        }

        // validate and set authentication
        if (email != null
                && SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            UserDetails user =
                    userDetailsService
                            .loadUserByUsername(email);

            if (jwtUtil.isValid(token)) {

                // ✅ Extract extra info from token
                Long   userId    =
                        jwtUtil.extractUserId(token);
                Long   roleId    =
                        jwtUtil.extractRoleId(token);
                String role      =
                        jwtUtil.extractRole(token);
                java.util.Date loginTime =
                        jwtUtil.extractLoginTime(token);
                java.util.Date expTime   =
                        jwtUtil.extractExpTime(token);

                // ✅ Set as request attributes
                // so any controller can read them
                request.setAttribute(
                        "userId",    userId);
                request.setAttribute(
                        "roleId",    roleId);
                request.setAttribute(
                        "role",      role);
                request.setAttribute(
                        "email",     email);
                request.setAttribute(
                        "loginTime", loginTime);
                request.setAttribute(
                        "expTime",   expTime);

                var auth =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities());

                auth.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }
}