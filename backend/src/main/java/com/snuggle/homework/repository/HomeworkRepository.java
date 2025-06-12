package com.snuggle.homework.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


}
