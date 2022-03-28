package com.saylk.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;


@Component
public class JwtUtil {

    private static final String secret = "jIJ29Jj#GH26";

    public String createToken(String username) {
        String token = Jwts.builder()
                .setHeaderParam("typ","jwt")
                .setHeaderParam("alg","HS256")
                .claim("username",username)
                .claim("role","admin")
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60))
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
        return token;
    }

    public void parseToken(String token) {
        Claims body = Jwts
                .parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        System.out.println(body.getSubject());
    }

}
