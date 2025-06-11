package com.snuggle.homework.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.snuggle.homework.domain.dto.UserResponse;
import com.snuggle.homework.domain.dto.HomeworkResponse;
import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.service.UserService;
import com.snuggle.homework.service.HomeworkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final HomeworkService homeworkService;

    // 전체 회원 조회
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.findAll().stream()
            .map(UserResponse::fromEntity)
            .collect(Collectors.toList());  // collect(Collectors.toList()) 사용
        return ResponseEntity.ok(users);
    }

    // 회원 상세 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        UserResponse user = userService.findById(userId)
            .map(UserResponse::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }

    // 특정 날짜 숙제 조회
    @GetMapping("/{userId}/homeworks")
    public ResponseEntity<List<HomeworkResponse>> getHomeworks(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        User user = userService.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        HomeworkResponse hw = homeworkService.getHomeworkByDate(user.getPhoneNumber(), date);

        // hw가 null 이면 빈 리스트, 아니면 singleton 리스트
        List<HomeworkResponse> list = (hw == null)
            ? Collections.emptyList()
            : Collections.singletonList(hw);

        return ResponseEntity.ok(list);
    }
}
