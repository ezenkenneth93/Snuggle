package com.snuggle.homework.controller;


import com.snuggle.homework.domain.dto.UserResponse;
import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 인증된 사용자의 정보를 조회합니다.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(Authentication authentication) {
        String phoneNumber = authentication.getName();
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UserResponse response = new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getPhoneNumber(),
                user.getEnglishLevel(),
                user.getJoinDate(),
                user.getExpirationDate()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 전화번호를 파라미터로 받아 사용자 정보를 조회합니다.
     * 예시: GET /api/user?phoneNumber=01012345678
     */
    @GetMapping
    public ResponseEntity<UserResponse> getUserByPhone(@RequestParam String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("해당 전화번호의 사용자를 찾을 수 없습니다."));

        UserResponse response = new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getPhoneNumber(),
                user.getEnglishLevel(),
                user.getJoinDate(),
                user.getExpirationDate()
        );
        return ResponseEntity.ok(response);
    }
}
