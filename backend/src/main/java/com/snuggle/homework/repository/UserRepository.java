package com.snuggle.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snuggle.homework.domain.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 휴대폰 번호로 사용자 찾기
    Optional<User> findByPhoneNumber(String phoneNumber);

    // 이름과 휴대폰 번호로 사용자 찾기 (로그인용)
    Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);

}

