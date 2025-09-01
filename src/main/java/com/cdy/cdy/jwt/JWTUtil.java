package com.cdy.cdy.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    public JWTUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {

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

    public String createJwt(String userEmail, String role, long expireMs) {
            long now = System.currentTimeMillis();

            var builder = Jwts.builder()
                    .claim("email", userEmail)    // 필요한 최소 정보
                    .issuedAt(new Date(now))
                    .expiration(new Date(now + expireMs));

            if (role != null) {                   // null이면 아예 넣지 않음
                builder.claim("role", role);
            }

            return builder
                    .signWith(secretKey)          // 서버 비밀키로 서명(위조 방지)
                    .compact();
        }


    public String createRefreshToken(Long userId, String role) {
        long refreshExpireMs = 1000L * 60 * 60 * 24 * 7; // 7일
        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpireMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

}
