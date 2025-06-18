package com.snuggle.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.snuggle.homework.service.KakaoOAuthService;
import com.snuggle.homework.domain.dto.KakaoUser;
import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.jwt.JwtTokenProvider;
import com.snuggle.homework.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/oauth/kakao")
@RequiredArgsConstructor
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 프론트에서 전달된 인가코드를 통해 카카오 로그인 처리
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String code = request.get("code"); // 프론트에서 전달된 인가 코드
        log.info("Controller/전달받은 코드 : {}", code);

        try {
            // 1. 인가코드로 카카오 사용자 정보 조회
            KakaoUser kakaoUser = kakaoOAuthService.loginWithKakao(code);
            log.info("Controller/KakaoUser : {}", kakaoUser);

            // 2. 전화번호로 유저 검색 (전화번호 동의받은 경우)
            Optional<User> optionalUser = userRepository.findByPhoneNumber(kakaoUser.getNormalizedPhoneNumber());
            log.info("Controller/전화번호 : {}", kakaoUser.getNormalizedPhoneNumber());
            log.info("Controller/조회된 회원 : {}", optionalUser);

            if (optionalUser.isEmpty()) {
                // 유저 정보가 없으면 프론트에서 추가정보 입력 페이지로 유도
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 3. 유저 존재 → JWT 발급 후 반환
            User user = optionalUser.get();
            String token = jwtTokenProvider.createToken(user.getPhoneNumber());
            // ✅ 여기서 로그 찍기 (로그인 성공 지점)
            log.info("✅ 로그인 성공 - JWT 발급 완료: {}", token);
            
            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("카카오 로그인 실패: " + e.getMessage());
        }
    }
}
