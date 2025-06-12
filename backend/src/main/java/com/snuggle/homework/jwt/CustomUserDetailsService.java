package com.snuggle.homework.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + phoneNumber));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getPhoneNumber())
                .password("") // JWT 방식이므로 비밀번호는 사용하지 않음
                .authorities("ROLE_USER")
                .build();
    }
}
