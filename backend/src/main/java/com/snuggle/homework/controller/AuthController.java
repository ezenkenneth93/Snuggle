package com.snuggle.homework.controller;

import com.snuggle.homework.domain.dto.LoginRequest;
import com.snuggle.homework.domain.dto.LoginResponse;
import com.snuggle.homework.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // 프론트엔드 연동을 위한 CORS 설정 (개발용)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.getPhoneNumber(), loginRequest.getName());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    

}
