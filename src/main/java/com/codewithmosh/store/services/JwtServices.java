package com.codewithmosh.store.services;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtServices {

   private final JwtConfig jwtConfig;

    //method for generating  access token with the email
    public Jwt generateAccessToken(User user) {
        //System.out.println("Access Token Expiration: " + jwtConfig.getAccessTokenExpiration());
        return getToken(user, jwtConfig.getAccessTokenExpiration());
    }

    //method for generating refresh token with the email
    public Jwt generateRefreshToken(User user) {
        //System.out.println("Refresh Token Expiration: " + jwtConfig.getRefreshTokenExpiration());
        return getToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private Jwt getToken(User user, long tokenExpiration) {
       var claims =  Jwts.claims()
                .subject(user.getId().toString())
                .add("email", user.getEmail())
                .add("role", user.getRole())
                .add("userName", user.getName())
                .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
                .build(); //

        return new Jwt(claims, jwtConfig.getSecretKey());
    }


    //method for validate toke
//    public Boolean validateToken(String token) {
//        try {
//            var claims = getClaims(token);
//
//            return claims.getExpiration().after(new Date());
//        } catch (JwtException e) {
//            return false;
//        }
//    }


    public Jwt parseToken(String token) {
        try {
            var claims = getClaims(token);
            return new Jwt(claims, jwtConfig.getSecretKey());
        } catch (JwtException e) {
            return null;
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
//    public Long getUserIdFromToken(String token) {
//        var claims = getClaims(token);
//
//        return Long.valueOf(claims.getSubject());
//    }

    //Get roles from the token
//    public String getRoleFromToken(String token) {
//        var claims = getClaims(token);
//        return claims.get("Role", String.class);
//    }
}