package com.snuggle.homework.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitRankDto {
    private Long userId;
    private String userName;
    private String phoneNumber;
    private int submissionCount; // ← 쿼리에서 submission_count로 넘어옴
    private int ranking;         // ← 쿼리에서 ranking으로 넘어옴

    public static String maskName(String name) {
        if (name == null || name.length() < 2) return name;
        if (name.length() == 2) return name.charAt(0) + "*";
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;

        if (phone.contains("-")) {
            int lastDash = phone.lastIndexOf("-");
            return phone.substring(0, phone.indexOf("-") + 1) + "****" + phone.substring(lastDash);
        }

        if (phone.length() == 11) {
            return phone.substring(0, 3) + "-****-" + phone.substring(7);
        }

        return phone;
    }

    // 새 쿼리 결과용 정적 팩토리 메서드
    public static SubmitRankDto from(Long userId, String userName, String phoneNumber, Long submissionCount, Integer ranking) {
        return new SubmitRankDto(
            userId,
            maskName(userName),
            maskPhone(phoneNumber),
            submissionCount != null ? submissionCount.intValue() : 0,
            ranking != null ? ranking : 0
        );
    }
}
