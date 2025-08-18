package com.cdy.cdy.jwt;


import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;
    private final long expiration;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret,
                   @Value("${spring.jwt.expiration}") long expiration) {

        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.expiration = expiration;
    }
    //매개변수의 token(jwt)을 받고 verifywith로 검증을함 우리가 가진 secretkey와 맞는지 ! 아닌지!
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload()
                .get("username", String.class);

    }

    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload().getExpiration()
                .before(new Date());
    }

    public String createJwt(String userEmail, String role, Long expireMs) {
        return Jwts.builder()
                .claim("email", userEmail)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                //*1000 안하니까 바로 만료됨 뭐가 문젠지 확인해볼것.
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }
}
