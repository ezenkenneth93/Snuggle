package com.snuggle.homework.domain.dto;

/**
 * 카카오 사용자 정보 DTO
 */
public class KakaoUser {
    private final String kakaoId;
    private final String normalizedPhoneNumber;

    public KakaoUser(String kakaoId, String normalizedPhoneNumber) {
        this.kakaoId = kakaoId;
        this.normalizedPhoneNumber = normalizedPhoneNumber;
    }

    public String getKakaoId() {
        return kakaoId;
    }

    public String getNormalizedPhoneNumber() {
        return normalizedPhoneNumber;
    }
}

