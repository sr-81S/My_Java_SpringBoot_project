package com.codewithmosh.store.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;


public class Jwt {
    private final Claims claims;
    private final SecretKey secretKey;

    public Jwt(Claims claims, SecretKey secretKey) {
        this.claims = claims;
        this.secretKey = secretKey;
    }

    //is valid function to check the token valid or not
    public Boolean isExpired(String token) {
        return claims.getExpiration().before(new Date());
    }


    //
    public Long getUserId(){
        return Long.valueOf(claims.getSubject());
    }

    //get role from token
    public String getRole(){
//        System.out.println(claims.get("role", String.class));
        return claims.get("role", String.class);
    }

    //to String method
    public String toString(){
       return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }
}
