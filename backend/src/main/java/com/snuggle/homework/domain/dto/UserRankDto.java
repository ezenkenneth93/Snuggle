package com.snuggle.homework.domain.dto;

public class UserRankDto {
    private Long userId;
    private String userName;
    private int count;
    private int rank;

    public UserRankDto(Long userId, String userName, int count, int rank) {
        this.userId = userId;
        this.userName = userName;
        this.count = count;
        this.rank = rank;
    }

    // getter 생략 가능 (Lombok 써도 됨)
}

