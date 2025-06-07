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
                .userHomework(dto.getUserHomework())
                .userQuestion(dto.getUserQuestion())
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


        List<Homework> list = homeworkRepository
        .findByUserUserIdAndSubmittedAtBetween(user.getUserId(), start, end);

        if (list.isEmpty()) {
            return null; // 또는 빈 DTO, 또는 "숙제 없음" 메시지 포함한 Response
        }

        Homework hw = list.get(0); // 첫 번째 숙제만 사용

            return toResponse(hw);
        }

    // 내부 변환 메서드: Entity → DTO
    private HomeworkResponse toResponse(Homework hw) {
        return HomeworkResponse.builder()
            .id(hw.getId())
            .userHomework(hw.getUserHomework())
            .userQuestion(hw.getUserQuestion())
            .aiFeedback(hw.getAiFeedback())
            .aiAnswer(hw.getAiAnswer())
            .submittedAt(hw.getSubmittedAt())
            .build();
    }

}