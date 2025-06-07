package com.snuggle.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.snuggle.homework.domain.entity.Homework;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    // 숙제 저장 메서드는 이미 JPARepository에 선언 되어있다.

    // 로그인한 사용자의 숙제만 조회
    List<Homework> findByUser_UserId(Long userId);

    // 숙제 상세조회
    Optional<Homework> findByIdAndUser_UserId(Long id, Long userId);

    // UserId와 날짜로 숙제 상세 조회
    List<Homework> findByUserUserIdAndSubmittedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
