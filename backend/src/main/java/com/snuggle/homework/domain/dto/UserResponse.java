package com.snuggle.homework.domain.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
} 
