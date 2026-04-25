package com.crm.crm_lite.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "verysecretkeyverysecretkeyverysecretkey";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // FIX: include userId as a claim so the frontend knows who the current user is.
    // Previously the token only stored email (subject) — no id — so currentUser?.id
    // was always undefined on the frontend, making isOwner always false.
    public String generateToken(String email, Long userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)          // ← NEW: embed userId
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}