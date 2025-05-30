package com.snuggle.homework.domain.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String name;
    private String phoneNumber;
    private Integer englishLevel;
    private LocalDate joinDate;
    private LocalDate expirationDate;
}
