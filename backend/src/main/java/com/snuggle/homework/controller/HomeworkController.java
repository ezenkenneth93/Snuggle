package com.snuggle.homework.controller;

import com.snuggle.homework.domain.dto.HomeworkRequest;
import com.snuggle.homework.domain.dto.HomeworkResponse;
import com.snuggle.homework.service.HomeworkService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/homeworks")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;

    // 1. 숙제 제출
    @PostMapping
    public ResponseEntity<HomeworkResponse> submitHomework(@RequestBody HomeworkRequest request, Principal principal) {
        String phoneNumber = principal.getName(); // JWT에서 추출된 사용자 식별자 .setSubject(여기) 여기에 담긴 값이 반환됨됨
        HomeworkResponse response = homeworkService.submitHomework(request, phoneNumber);
        return ResponseEntity.ok(response);
    }

    // 2. 내 숙제 전체 조회
    @GetMapping("/me")
    public ResponseEntity<List<HomeworkResponse>> getMyHomeworks(Principal principal) {
        String phoneNumber = principal.getName();
        List<HomeworkResponse> responses = homeworkService.getMyHomeworks(phoneNumber);
        return ResponseEntity.ok(responses);
    }

    // 3. 내 숙제 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<HomeworkResponse> getMyHomeworkDetail(@PathVariable Long id, Principal principal) {
        String phoneNumber = principal.getName();
        HomeworkResponse response = homeworkService.getMyHomeworkDetail(id, phoneNumber);
        return ResponseEntity.ok(response);
    }

    // 4. 날짜로 숙제조회
    @GetMapping("/me/date")
    public ResponseEntity<HomeworkResponse> getMyHomeworkByDate(
                @RequestParam("date") String rawDate,
                Principal principal
        ) {
            String phoneNumber = principal.getName(); // 토큰에서 추출된 사용자 식별값
            
            // 전처리 및 변환
            String trimmed = rawDate.strip(); // 앞뒤 공백 제거
            LocalDate date;
            try {
                date = LocalDate.parse(trimmed); // ISO 형식 파싱 (예: 2025-06-06)
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. 예: 2025-06-07 형식으로 입력하세요.");
            }

            
            HomeworkResponse response = homeworkService.getHomeworkByDate(phoneNumber, date);
            return ResponseEntity.ok(response);
        }

}
