package com.snuggle.homework.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeworkRequest {
    private String userHomework;    // 기존: homeworkContent
    private String userQuestion;    // 기존: userAnswer
}