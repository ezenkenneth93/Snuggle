package com.snuggle.homework.domain.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phoneNumber;
    private String name;
}