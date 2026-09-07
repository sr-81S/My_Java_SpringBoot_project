package com.codewithmosh.store.services;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtServices {

   private final JwtConfig jwtConfig;

    //method for generating  access token with the email
    public String generateAccessToken(User user) {
        return getToken(user, jwtConfig.getAccessTokenExpirationMs());
    }

    //method for generating refresh token with the email
    public String generateRefreshToken(User user) {
        return getToken(user, jwtConfig.getRefreshTokenExpirationMs());
    }

    private String getToken(User user, long tokenExpiration) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("userName", user.getName())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000)) // 5 minutes
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }


    //method for validate toke
    public Boolean validateToken(String token) {
        try {
            var claims = getClaims(token);

            return claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                 .verifyWith(jwtConfig.getSecretKey())
                 .build()
                 .parseSignedClaims(token)
                .getPayload();
    }

    //Function for get user ID from token
    public Long getUserIdFromToken(String token) {
        var claims = getClaims(token);

        return Long.valueOf(claims.getSubject());
    }
}