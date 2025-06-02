package com.snuggle.homework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snuggle.homework.domain.dto.LoginRequest;
import com.snuggle.homework.domain.dto.UserResponse;
import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.jwt.JwtTokenProvider;
import com.snuggle.homework.repository.UserRepository;
import com.snuggle.homework.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userService.findByNameAndPhoneNumber(request.getName(), request.getPhoneNumber())
                .map(user -> {
                    String token = jwtTokenProvider.createToken(user.getPhoneNumber());
                    log.info("✅ 로그인 성공 - 이름: {}, 전화번호: {}", request.getName(), request.getPhoneNumber());
                    return ResponseEntity.ok(token);  // 로그인 성공 시 JWT 반환
                })
                .orElseGet(() -> {
                    log.warn("❌ 로그인 실패 - 이름: {}, 전화번호: {}", request.getName(), request.getPhoneNumber());
                    return ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body("이름 또는 전화번호가 일치하지 않습니다");
                });
    }


    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = auth.getName();

        // ✅ 이제 Repository 대신 Service를 사용
        User user = userService.findByPhoneNumber(phoneNumber);

        UserResponse response = UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .englishLevel(user.getEnglishLevel())
                .joinDate(user.getJoinDate())
                .expirationDate(user.getExpirationDate())
                .build();

        log.info("=== [SecurityContext 인증 정보] ===");
        log.info("isAuthenticated : {}", auth.isAuthenticated());
        log.info("Principal       : {}", auth.getPrincipal());
        log.info("Authorities     : {}", auth.getAuthorities());
        log.info("이름 (getName)   : {}", auth.getName());
        
        return ResponseEntity.ok(response);
    }

}
