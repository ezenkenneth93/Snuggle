package com.snuggle.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.snuggle.homework.service.KakaoOAuthService;
import com.snuggle.homework.domain.dto.ExtraInfoRequest;
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
            User user = kakaoOAuthService.loginWithKakao(code); // User를 직접 반환
            log.info("Controller/KakaoUser : {}", user);

            // 2. 만약 조회된 사용자가 없으면
            if (user == null) {
                // ✅ kakaoId를 응답에 담아서 보냄
                String kakaoId = kakaoOAuthService.getLastRequestedKakaoId();  // (아래에 설명 있음)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body(Map.of("message", "회원 정보 없음", "kakaoId", kakaoId));
            }

            // 3. 유저 존재 → JWT 발급 후 반환
            String token = jwtTokenProvider.createToken(user.getPhoneNumber());
            // ✅ 여기서 로그 찍기 (로그인 성공 지점)
            log.info("✅ 로그인 성공 - JWT 발급 완료: {}", token);
            
            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("카카오 로그인 실패: " + e.getMessage());
        }
    }

    /**
     * 
     */
    @PostMapping("/extra-info")
    // @RequestBody로 JSON 바디를 ExtraInfoRequest DTO로 매핑
    // 이후 dto.phoneNumber(), dto.name(), dto.kakaoId()로 각각 꺼냄
    public ResponseEntity<?> connectExtraInfo(@RequestBody ExtraInfoRequest dto) {

        try {
            User user = kakaoOAuthService.handleExtraInfo(dto.phoneNumber(), dto.name(), dto.kakaoId());
            log.info("Extra-Info/KakaoUser : {}", user.getName());
            log.info("Extra-Info/KakaoUser : {}", user.getPhoneNumber());

            String token = jwtTokenProvider.createToken(user.getPhoneNumber());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("정보가 일치하지 않습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 연결된 카카오 계정입니다.");
        }
    }

}
