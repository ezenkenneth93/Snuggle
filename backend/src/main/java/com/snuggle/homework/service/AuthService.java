package com.snuggle.homework.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.repository.UserRepository;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    // application.properties에 정의된 비밀 키
    @Value("${jwt.secret}")
    private String secretKey;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String login(String phoneNumber, String name) {
        Optional<User> optionalUser = userRepository.findByNameAndPhoneNumber(name, phoneNumber);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return generateToken(user);
        } else {
            throw new RuntimeException("로그인 실패: 사용자 정보가 일치하지 않습니다.");
        }
    }

    
    // 로그인 후 토큰 생성
    private String generateToken(User user) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        long now = System.currentTimeMillis();
        return Jwts.builder()
            .setSubject(user.getPhoneNumber())
            .claim("name", user.getName())
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + 1000L * 60 * 60 * 24))  // 24시간
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
}
