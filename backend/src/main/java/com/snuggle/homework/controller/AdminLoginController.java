package com.snuggle.homework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snuggle.homework.domain.dto.AdminLoginRequest;
import com.snuggle.homework.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminLoginController {

    private final DaoAuthenticationProvider adminAuthenticationProvider;
    private final JwtTokenProvider jwtTokenProvider;

    private static final Logger log = LoggerFactory.getLogger(AdminLoginController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest request) {
        try {
            log.info("🛠 관리자 로그인 시도 - username: {}", request.getUsername());

            Authentication authentication = adminAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            String username = authentication.getName();
            String role     = authentication.getAuthorities()
                                         .iterator()
                                         .next()
                                         .getAuthority();  // "ROLE_ADMIN"

            String token = jwtTokenProvider.createToken(username, role);
            log.info("✅ 관리자 로그인 성공 - username: {}", username);
            return ResponseEntity.ok(token);

        } catch (AuthenticationException ex) {
            log.warn("❌ 관리자 로그인 실패 - username: {} / reason: {}", 
                     request.getUsername(), ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("관리자 로그인 실패: " + ex.getMessage());
        }
    }
}
