package com.snuggle.homework.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    // application.properties에 설정된 비밀 키 (Base64 인코딩된 문자열)
    @Value("${jwt.secret}")
    private String secretKey;

    // JWT 유효 시간: 1시간 (밀리초 기준)
    private final long validityInMilliseconds = 3600000;

    /**
     * JWT 토큰 생성 메서드
     * - phoneNumber를 subject로 저장
     * - role 정보를 claims에 추가 ("ROLE_USER")
     */
    public String createToken(String phoneNumber) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        // 비밀키로 HMAC 서명 키 생성
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        
        // subject는 사용자 식별자 (Principal.getName()에서 사용됨)
        Claims claims = Jwts.claims().setSubject(phoneNumber);
        claims.put("role", "ROLE_USER"); // 🔐 권한 정보 추가

        return Jwts.builder()
                .setClaims(claims)           // 사용자 정보 포함
                .setIssuedAt(now)            // 발급 시간
                .setExpiration(validity)     // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘
                .compact();                  // JWT 문자열 생성
    }

    /**
     * JWT에서 사용자 phoneNumber 추출
     * - subject는 JWT 생성 시 phoneNumber로 설정됨
     */
    public String getPhoneNumber(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)       // JWT 파싱
                .getBody()
                .getSubject();               // subject = phoneNumber
    }

    /**
     * JWT의 유효성 검사
     * - 서명 검증 및 만료 시간 확인
     * - 예외 발생 시 false 반환
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token); // 파싱이 성공하면 유효한 토큰
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * JWT에서 role 정보 추출
     * - 권한 기반 접근 제어를 위해 사용됨
     */
    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class); // "role" 클레임에서 문자열 추출
    }

}
