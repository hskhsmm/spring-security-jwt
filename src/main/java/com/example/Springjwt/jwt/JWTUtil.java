package com.example.Springjwt.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    // JWT 서명을 위한 SecretKey 객체
    private SecretKey secretKey;

    /**
     * 생성자 - application.yml에서 설정한 JWT 시크릿 값을 받아 SecretKey 객체로 초기화
     * @param secret application.yml의 spring.jwt.secret 값
     */
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        // 문자열 시크릿을 바이트 배열로 변환한 뒤, HMAC-SHA256 알고리즘을 사용하는 SecretKey로 생성
        secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    /**
     * JWT 토큰에서 username 클레임을 추출
     * @param token JWT 문자열
     * @return username 값
     */
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 시크릿키로 서명 검증
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    /**
     * JWT 토큰에서 role 클레임을 추출
     * @param token JWT 문자열
     * @return role 값
     */
    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    /**
     * JWT 토큰의 만료 여부를 확인
     * @param token JWT 문자열
     * @return true면 만료됨, false면 유효함
     */
    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    /**
     * JWT 토큰 생성
     * @param username 사용자 이름
     * @param role 사용자 역할
     * @param expiredMs 토큰 유효시간(ms)
     * @return 생성된 JWT 문자열
     */
    public String createJwt(String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("username", username)  // 사용자 이름 클레임
                .claim("role", role)          // 사용자 역할 클레임
                .issuedAt(new Date(System.currentTimeMillis()))  // 발급 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs))  // 만료 시간
                .signWith(secretKey) // 서명
                .compact(); // JWT 문자열 반환
    }
}
