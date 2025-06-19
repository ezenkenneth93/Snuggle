package com.snuggle.homework.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.snuggle.homework.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 휴대폰 번호로 사용자 찾기
    Optional<User> findByPhoneNumber(String phoneNumber);

    // 이름과 휴대폰 번호로 사용자 찾기 (로그인용)
    Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);

    // 이미 kakaoId를 등록한 회원의 경우
    Optional<User> findByKakaoId(String kakaoId);

    @Query(value = "SELECT COUNT(*) FROM user_info WHERE kakao_id = :kakaoId AND ROWNUM = 1", nativeQuery = true)
    int countByKakaoIdManual(@Param("kakaoId") String kakaoId);



}

