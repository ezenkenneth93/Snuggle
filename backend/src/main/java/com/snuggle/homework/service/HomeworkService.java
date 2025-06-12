package com.snuggle.homework.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.snuggle.homework.domain.dto.HomeworkRequest;
import com.snuggle.homework.domain.dto.HomeworkResponse;
import com.snuggle.homework.domain.entity.Homework;
import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.domain.exception.DuplicateSubmissionException;
import com.snuggle.homework.repository.HomeworkRepository;
import com.snuggle.homework.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final UserRepository userRepository;

    private final GptService gptService;   // ← 추가

    // 1. 숙제 제출
    public HomeworkResponse submitHomework(HomeworkRequest dto, String phoneNumber) {
        // 1) 사용자 조회
        User user = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 1-1) 숙제를 제출했는지 여부 : countBy 로 오늘 제출 여부 확인
        Long userId = user.getUserId();
        if (homeworkRepository.countByUser_UserIdAndSubmittedDate(userId, LocalDate.now()) > 0) {
            throw new DuplicateSubmissionException("오늘 이미 숙제를 제출하셨습니다.");
        }

        // 2) GPT 서버에 피드백 요청
        String feedback = gptService.getFeedback(dto.getUserHomework(), dto.getUserQuestion());

        // 3) 엔티티 생성 (GPT 피드백을 aiFeedback 필드에 담아서)
        Homework homework = Homework.builder()
            .user(user)
            .userHomework(dto.getUserHomework())
            .userQuestion(dto.getUserQuestion())
            .aiFeedback(feedback)         // ← 피드백 저장
            .aiAnswer("")                 // 필요하면 따로 채워도 좋아
            .build();

        // 4) DB에 저장
        Homework saved = homeworkRepository.save(homework);

        // 5) DTO로 변환 후 반환
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