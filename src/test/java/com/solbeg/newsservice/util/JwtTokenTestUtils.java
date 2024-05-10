package com.solbeg.newsservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JwtTokenTestUtils {

    private static final String SECRET_KEY = "73357638792F423F4528482B4D6251655468576D5A7133743677397A24432646";

    public static String generateToken(String userEmail, UUID userId, List<String> roles) {
        Instant now = Instant.now();
        Instant expiry = now.plus(1, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(userEmail)
                .claim("id", userId)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }
}