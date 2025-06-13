package com.snuggle.homework.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.snuggle.homework.domain.dto.HomeworkRequest;
import com.snuggle.homework.domain.dto.HomeworkResponse;
import com.snuggle.homework.domain.dto.SubmitRankDto;
import com.snuggle.homework.domain.dto.UserRankDto;
import com.snuggle.homework.domain.entity.Homework;
import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.domain.exception.DuplicateSubmissionException;
import com.snuggle.homework.repository.HomeworkRepository;
import com.snuggle.homework.repository.UserHomeworkCountProjection;
import com.snuggle.homework.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final UserRepository userRepository;

    private final GptService gptService;   // ← 추가

    // 0. 오늘 제출여부
    public boolean hasSubmittedToday(Long userId) {
        return homeworkRepository.countByUser_UserIdAndSubmittedDate(userId, LocalDate.now()) > 0;
    }

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

    // 숙제 제출한 날짜 반환
    public List<LocalDate> getSubmittedDates(Long userId) {
        return homeworkRepository.findSubmittedDatesByUserId(userId);
    }

    // 현재까지 숙제 몇번 제출했는지?
    public int getTotalHomeworkCount(Long userId) {
        return homeworkRepository.countByUserId(userId);
    }

    // 연속으로 제출한 날은 며칠인지?
    public int getConsecutiveSubmitDays(Long userId) {
        List<LocalDate> dates = homeworkRepository.findSubmittedDates(userId)
                                    .stream()
                                    .map(LocalDateTime::toLocalDate)
                                    .distinct()
                                    .toList();

        if (dates.isEmpty()) return 0;

        // 빠른 조회를 위해 Set으로 변환
        Set<LocalDate> dateSet = new HashSet<>(dates);

        LocalDate today = LocalDate.now();
        int streak = 0;

        // 오늘부터 하루씩 줄이면서 연속 제출 여부 확인
        while (dateSet.contains(today.minusDays(streak))) {
            streak++;
        }

        return streak;
    }

    // 회원 전체 랭킹
    public List<SubmitRankDto> getMaskedMonthlySubmitRanking() {
        List<SubmitRankDto> rawList = homeworkRepository.findMonthlyRankingDto();
        AtomicInteger rank = new AtomicInteger(1);  // ✅ 여기만 바꿈

        return rawList.stream()
            .map(dto -> SubmitRankDto.from(
                dto.getUserId(),
                dto.getUserName(),
                dto.getPhoneNumber(),
                dto.getCount(),
                rank.getAndIncrement())  // ✅ 안전하게 증가
            )
            .toList();
    }

    public SubmitRankDto getMyMonthlyRank(Long userId) {
        List<SubmitRankDto> rawList = homeworkRepository.findMonthlyRankingDto();

        AtomicInteger rank = new AtomicInteger(1);

        return rawList.stream()
            .map(dto -> SubmitRankDto.from(
                dto.getUserId(),
                dto.getUserName(),
                dto.getPhoneNumber(),
                dto.getCount(),
                rank.getAndIncrement()
            ))
            .filter(dto -> dto.getUserId().equals(userId))  // ✅ 내 아이디만 반환
            .findFirst()
            .orElse(null);
    }
}