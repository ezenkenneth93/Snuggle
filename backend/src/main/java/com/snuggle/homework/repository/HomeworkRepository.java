package com.snuggle.homework.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.snuggle.homework.domain.dto.SubmitRankDto;
import com.snuggle.homework.domain.entity.Homework;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    // 숙제 저장 메서드는 이미 JPARepository에 선언 되어있다.

    // 로그인한 사용자의 숙제만 조회
    List<Homework> findByUser_UserId(Long userId);

    // 숙제 상세조회
    Optional<Homework> findByIdAndUser_UserId(Long id, Long userId);

    // UserId와 날짜로 숙제 상세 조회
    List<Homework> findByUserUserIdAndSubmittedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    // Oracle 호환을 위해 existsBy 대신 countBy 사용
    long countByUser_UserIdAndSubmittedDate(Long userId, LocalDate submittedDate);

    // 예: 사용자 ID와 월(또는 기간)을 받아 제출 날짜 리스트를 반환
    @Query("SELECT h.submittedDate FROM Homework h WHERE h.user.userId = :userId")
    List<LocalDate> findSubmittedDatesByUserId(@Param("userId") Long userId);

    // 현재까지 숙제를 몇 번 제출했는가?
    @Query("SELECT COUNT(h) FROM Homework h WHERE h.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);

    // 연속으로 숙제 제출한 날이 며칠인가?
    @Query("SELECT h.submittedAt FROM Homework h WHERE h.user.id = :userId ORDER BY h.submittedAt DESC")
    List<LocalDateTime> findSubmittedDates(@Param("userId") Long userId);

    // ↓ 이렇게 변경
    @Query(value = """
        SELECT
            u.user_id AS userId,
            u.name AS name,
            u.phone_number AS phoneNumber,
            sub.submission_count AS submissionCount,
            sub.ranking AS ranking
        FROM (
            SELECT
                h.user_id,
                COUNT(h.id) AS submission_count,
                DENSE_RANK() OVER (ORDER BY COUNT(h.id) DESC) AS ranking
            FROM homework h
            WHERE TO_CHAR(h.submitted_at, 'YYYY-MM') = TO_CHAR(SYSDATE, 'YYYY-MM')
            GROUP BY h.user_id
        ) sub
        JOIN user_info u ON u.user_id = sub.user_id
    """, nativeQuery = true)
    List<Object[]> findMonthlyRankingRaw();
}
