package com.snuggle.homework.domain.dto;

import java.time.LocalDate;

import org.springframework.beans.BeanUtils;

import com.snuggle.homework.domain.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String name;
    private String phoneNumber;
    private Integer englishLevel;
    private LocalDate joinDate;
    private LocalDate expirationDate;

    public static UserResponse fromEntity(User u) {
        UserResponse r = new UserResponse();
        BeanUtils.copyProperties(u, r);
        return r;
    }
} 
