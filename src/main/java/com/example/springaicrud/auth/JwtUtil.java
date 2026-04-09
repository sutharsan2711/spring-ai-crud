package com.example.springaicrud.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // build signing key
    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ==========================================
    // ✅ Generate token WITH userId and roleId
    // ==========================================
    public String generateToken(
            String email,
            Long   userId,
            Long   roleId,
            String roleName) {

        // ✅ Extra claims added to payload
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",  userId);   // user id
        claims.put("roleId",  roleId);   // role id
        claims.put("role",    roleName); // role name

        Date now    = new Date();
        Date expiry = new Date(
                now.getTime() + expiration);

        return Jwts.builder()
                // ✅ email as subject
                .setSubject(email)
                // ✅ add extra claims
                .addClaims(claims)
                // ✅ login time
                .setIssuedAt(now)
                // ✅ expiry time
                .setExpiration(expiry)
                .signWith(key(),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    // ==========================================
    // ✅ Extract email from token
    // ==========================================
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // ==========================================
    // ✅ Extract userId from token
    // ==========================================
    public Long extractUserId(String token) {
        Object userId =
                getClaims(token).get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    // ==========================================
    // ✅ Extract roleId from token
    // ==========================================
    public Long extractRoleId(String token) {
        Object roleId =
                getClaims(token).get("roleId");
        if (roleId instanceof Integer) {
            return ((Integer) roleId).longValue();
        }
        return (Long) roleId;
    }

    // ==========================================
    // ✅ Extract role name from token
    // ==========================================
    public String extractRole(String token) {
        return (String) getClaims(token)
                .get("role");
    }

    // ==========================================
    // ✅ Extract login time from token
    // ==========================================
    public Date extractLoginTime(String token) {
        return getClaims(token).getIssuedAt();
    }

    // ==========================================
    // ✅ Extract expiry time from token
    // ==========================================
    public Date extractExpTime(String token) {
        return getClaims(token).getExpiration();
    }

    // ==========================================
    // ✅ Validate token
    // ==========================================
    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException
                 | IllegalArgumentException e) {
            return false;
        }
    }

    // ==========================================
    // 🔧 Get all claims from token
    // ==========================================
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}