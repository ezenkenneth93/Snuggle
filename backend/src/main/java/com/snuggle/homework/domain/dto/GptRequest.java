package com.snuggle.homework.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GptRequest {
    @JsonProperty("english_essay")
    private String englishEssay;
    
    @JsonProperty("question")
    private String question;
}