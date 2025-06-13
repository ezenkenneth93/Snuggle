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
    private int count;
    private int rank;

    public SubmitRankDto(Long userId, String userName, String phoneNumber, Long count) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.count = count.intValue(); // ← 핵심
    }

    public static String maskName(String name) {
        if (name == null || name.length() < 2) return name;
        if (name.length() == 2) return name.charAt(0) + "*";
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;

        // "010-1234-5678" 형식이면
        if (phone.contains("-")) {
            int lastDash = phone.lastIndexOf("-");
            return phone.substring(0, phone.indexOf("-") + 1) + "****" + phone.substring(lastDash);
        }

        // "01012345678" 형식
        if (phone.length() == 11) {
            return phone.substring(0, 3) + "-****-" + phone.substring(7);
        }

        return phone;
    }


    public static SubmitRankDto from(Long userId, String userName, String phoneNumber, int count, int rank) {
        return new SubmitRankDto(
            userId,
            maskName(userName),
            maskPhone(phoneNumber),
            count,
            rank
        );
    }
}
