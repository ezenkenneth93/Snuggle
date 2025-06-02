package com.snuggle.homework.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 이름과 휴대폰 번호로 사용자 조회 (로그인 검증)
     */
    public Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber) {
        return userRepository.findByNameAndPhoneNumber(name, phoneNumber);
    }

    public User findByPhoneNumber(String phoneNumber) {
    return userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("User not found"));
}
}
