package com.snuggle.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.snuggle.homework.domain.dto.KakaoUser;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    /**
     * 전체 카카오 로그인 흐름 처리 (인가코드 → 액세스 토큰 → 사용자 정보)
     */
    public KakaoUser loginWithKakao(String code) {
        String accessToken = requestAccessToken(code);
        return requestUserInfo(accessToken);
    }

    /**
     * 카카오에 인가코드를 보내 access_token 요청
     */
    private String requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
            "https://kauth.kakao.com/oauth/token", request, Map.class
        );
        log.info("Service/Access_token : {}", response.getBody().get("access_token"));
        // 응답 JSON에서 access_token 추출
        return (String) response.getBody().get("access_token");
    }

    /**
     * access_token을 이용해 사용자 정보 요청
     */
    private KakaoUser requestUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Authorization: Bearer <token>

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.GET,
            request,
            Map.class
        );

        Map<String, Object> body = response.getBody();
        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");

        String rawPhone = (String) kakaoAccount.get("phone_number"); // "+82 10-xxxx-xxxx"
        String normalizedPhone = normalizePhone(rawPhone);

        String kakaoId = String.valueOf(body.get("id")); // 사용자 고유 ID

        log.info("Service/User_info : {}", new KakaoUser(kakaoId, normalizedPhone));

        return new KakaoUser(kakaoId, normalizedPhone);
    }

    /**
     * 전화번호 정규화: +82 → 0, 숫자만 남기기
     */
    private String normalizePhone(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("[^0-9]", "") // 숫자만 남기기
                    .replaceFirst("^82", "0"); // 국제번호 → 국내번호
    }
}

