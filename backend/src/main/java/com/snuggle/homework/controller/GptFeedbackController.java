package com.snuggle.homework.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snuggle.homework.service.GptService;

@RestController
@RequestMapping("/api/gpt")
public class GptFeedbackController {

    private final GptService gptService;

    public GptFeedbackController(GptService gptService) {
        this.gptService = gptService;
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> generateFeedback(@RequestBody Map<String, String> payload) {
        // JSON 키와 동일하게 꺼내 옵니다
        String essay    = payload.get("english_essay");
        String question = payload.getOrDefault("question", "");

        // 서비스 메서드 이름을 getFeedback 으로 호출
        String feedback = gptService.getFeedback(essay, question);
        return ResponseEntity.ok(feedback);
    }
}
