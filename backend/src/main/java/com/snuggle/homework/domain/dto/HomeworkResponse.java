package com.snuggle.homework.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HomeworkResponse {
    private Long id;
    private Long userId;
    private LocalDateTime submittedAt;
    
    private String userHomework;    // 기존: homeworkContent
    private String userQuestion;    // 기존: userAnswer
    private String aiFeedback;      // 기존: feedback
    private String aiAnswer;        // 기존: suggestedAnswer
}