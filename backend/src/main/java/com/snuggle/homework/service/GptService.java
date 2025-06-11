package com.snuggle.homework.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.snuggle.homework.domain.dto.GptRequest;
import com.snuggle.homework.domain.dto.GptResponse;

@Service
public class GptService {

    private final WebClient webClient;

    public GptService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8000") // Python GPT 서버 주소
                .build();
    }

    public String getFeedback(String englishEssay, String question) {
        GptRequest request = new GptRequest();
        request.setEnglishEssay(englishEssay);
        request.setQuestion(question);

        GptResponse response = webClient.post()
                // ← 여기만 변경합니다
                .uri("/generate-feedback")  
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GptResponse.class)
                .block(); // 동기 처리

        return response != null ? response.getResult() : "응답이 없습니다.";
    }
}
