package com.snuggle.homework.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.snuggle.homework.domain.dto.HomeworkRequest;
import com.snuggle.homework.domain.dto.HomeworkResponse;
import com.snuggle.homework.domain.dto.SubmitRankDto;
import com.snuggle.homework.domain.dto.UserRankDto;
import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.jwt.CustomUserDetails;
import com.snuggle.homework.repository.UserRepository;
import com.snuggle.homework.service.HomeworkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/homeworks")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final UserRepository userRepository;

    // 0. 오늘 제출 여부 조회
    @GetMapping("/today")
    public ResponseEntity<Boolean> hasSubmittedToday(
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal
    ) {
        // principal.getUsername() → phoneNumber
        String phone = principal.getUsername();
        User user = userRepository.findByPhoneNumber(phone)
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        boolean result = homeworkService.hasSubmittedToday(user.getUserId());
        return ResponseEntity.ok(result);
    }


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

    // 5. 내 제출 날짜 전체 조회
    @GetMapping("/me/dates")
    public ResponseEntity<List<LocalDate>> getMySubmittedDates(Principal principal) {
        String phone = principal.getName();
        User user = userRepository.findByPhoneNumber(phone)
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        List<LocalDate> dates = homeworkService.getSubmittedDates(user.getUserId());
        return ResponseEntity.ok(dates);
    }

    // 6. 이번달 회원 랭킹
    @GetMapping("/rank")
    public ResponseEntity<List<SubmitRankDto>> getSubmitRanking() {
        List<SubmitRankDto> ranking = homeworkService.getMaskedMonthlySubmitRanking();
        return ResponseEntity.ok(ranking);
    }

    // 7. 개인 랭킹 조회
    @GetMapping("/rank/me")
    public ResponseEntity<SubmitRankDto> getMyMonthlyRank(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        SubmitRankDto myRank = homeworkService.getMyMonthlyRank(userId);
        return ResponseEntity.ok(myRank);
    }




}
