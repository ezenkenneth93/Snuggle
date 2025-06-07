package com.snuggle.homework.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HomeworkResponse {
    private Long id;
    private String userAnswer;
    private String feedback;
    private String suggestedAnswer;
    private LocalDateTime submittedAt;
}