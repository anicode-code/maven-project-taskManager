package com.example.taskapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    public JwtUtil(@Value("${security.jwt.secret:}") String secret,
                   @Value("${security.jwt.expiration-ms:3600000}") long expirationMs) {

        if (secret == null) secret = "";

        byte[] keyBytes = tryDecodeBase64(secret);
        if (keyBytes == null) {
            // treat secret string bytes (UTF-8)
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        // Ensure keyBytes length >= 32 bytes. If not, derive a 32-byte key deterministically via SHA-256.
        if (keyBytes.length < 32) {
            keyBytes = sha256(secret);
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    private static byte[] tryDecodeBase64(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            // if s decodes to bytes and re-encoding equals original (tolerant), accept it
            byte[] decoded = Base64.getDecoder().decode(s);
            // If decoding produced something plausible (non-empty), return it.
            if (decoded.length > 0) return decoded;
        } catch (IllegalArgumentException ignored) {
            // not a base64 string
        }
        return null;
    }

    private static byte[] sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input == null ? new byte[0] : input.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            // fallback: pad/truncate to 32 bytes
            byte[] fallback = new byte[32];
            byte[] src = (input == null ? new byte[0] : input.getBytes(StandardCharsets.UTF_8));
            for (int i = 0; i < fallback.length; i++) fallback[i] = (i < src.length) ? src[i] : 0;
            return fallback;
        }
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
