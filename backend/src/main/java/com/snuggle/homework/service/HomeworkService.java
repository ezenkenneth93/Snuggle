package com.snuggle.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.snuggle.homework.domain.dto.HomeworkRequest;
import com.snuggle.homework.domain.dto.HomeworkResponse;
import com.snuggle.homework.domain.entity.Homework;
import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.repository.HomeworkRepository;
import com.snuggle.homework.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final UserRepository userRepository;

    // 1. 숙제 제출
    public HomeworkResponse submitHomework(HomeworkRequest dto, String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Homework homework = Homework.builder()
                .user(user)
                .userAnswer(dto.getUserAnswer())
                .build();

        Homework saved = homeworkRepository.save(homework);
        return toResponse(saved);
    }

    // 2. 내 숙제 전체 조회
    public List<HomeworkResponse> getMyHomeworks(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return homeworkRepository.findByUser_UserId(user.getUserId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 3. 내 숙제 상세 조회
    public HomeworkResponse getMyHomeworkDetail(Long homeworkId, String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Homework homework = homeworkRepository.findByIdAndUser_UserId(homeworkId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("제출하신 숙제가 없습니다."));

        return toResponse(homework);
    }

    // 날짜로 숙제조회
    public HomeworkResponse getHomeworkByDate(String phoneNumber, LocalDate date) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        LocalDateTime start = date.atStartOfDay();        // YYYY-MM-DD 00:00:00
        LocalDateTime end = start.plusDays(1);       // 다음 날 00:00:00 (exclusive)


        Homework hw = homeworkRepository
                .findByUserUserIdAndSubmittedAtBetween(user.getUserId(), start, end)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 숙제가 없습니다."));

        return toResponse(hw);
    }

        // 내부 변환 메서드: Entity → DTO
        private HomeworkResponse toResponse(Homework hw) {
                return HomeworkResponse.builder()
                        .id(hw.getId())
                        .userAnswer(hw.getUserAnswer())
                        .feedback(hw.getFeedback())
                        .suggestedAnswer(hw.getSuggestedAnswer())
                        .submittedAt(hw.getSubmittedAt())
                        .build();
        }

}