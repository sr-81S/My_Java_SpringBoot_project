package com.codewithmosh.store.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtServices {

    @Value("${spring.jwt.secret}")
    private String secret;

    //method for generating token with the email
    public String generateToken(String email) {

        final long tokenExpiration = 86400;
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000)) // 10 hours
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

    }

}