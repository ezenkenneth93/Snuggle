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
import com.snuggle.homework.domain.entity.User;
import com.snuggle.homework.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private String lastRequestedKakaoId;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    /**
     * kakaoId Getter
     */
    public String getLastRequestedKakaoId() {
        return lastRequestedKakaoId;
    }

    /**
     * 전체 카카오 로그인 흐름 처리 (인가코드 → 액세스 토큰 → 사용자 정보)
     */
    public User loginWithKakao(String code) {
        String accessToken = requestAccessToken(code);
        KakaoUser kakaoUser = requestUserInfo(accessToken);
        String kakaoId = kakaoUser.getKakaoId();

        // 1. kakaoId로 이미 등록된 유저인지 먼저 확인
        Optional<User> existingByKakaoId = userRepository.findByKakaoId(kakaoId);
        if (existingByKakaoId.isPresent()) {
            return existingByKakaoId.get(); // 이미 등록된 경우 즉시 로그인
        }

        // 2. phoneNumber로 유저 검색 (전화번호 제공된 경우)
        String phone = kakaoUser.getNormalizedPhoneNumber();
        if (phone != null) {
            Optional<User> byPhone = userRepository.findByPhoneNumber(phone);
            if (byPhone.isPresent()) {
                User user = byPhone.get();
                user.setKakaoId(kakaoId); // ✅ kakaoId 연결
                return userRepository.save(user); // 로그인 완료
            }
        }

        // 3. 전화번호도 없고 kakaoId도 미등록 → extra_info 페이지로 유도
        return null;
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

        this.lastRequestedKakaoId = kakaoId;  // kakaoId 추출한 후
        
        KakaoUser kakaoUser = new KakaoUser(kakaoId, normalizedPhone);
        
        log.info("==================================================================");
        log.info("Service/카카오아이디 : {}", kakaoUser.getKakaoId());
        log.info("Service/전화번호 : {}", kakaoUser.getNormalizedPhoneNumber());
        log.info("==================================================================");

        return kakaoUser;
    }

    /**
     * 전화번호 정규화: +82 → 0, 숫자만 남기기
     */
    private String normalizePhone(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("[^0-9]", "") // 숫자만 남기기
                    .replaceFirst("^82", "0"); // 국제번호 → 국내번호
    }

    // 카카오톡에 전화번호를 제공하지 않은 회원의 경우 : 따로 전화번호를 입력받아서 DB와 대조해보는 과정이다.
    public User handleExtraInfo(String phoneNumber, String name, String kakaoId) {
        // 1. 전화번호로 회원 조회 (추가 조건: 이름도 검사)
        User user = userRepository.findByPhoneNumber(phoneNumber)
            .filter(u -> u.getName().equals(name))
            .orElseThrow(() -> new IllegalArgumentException("입력한 정보와 일치하는 회원이 없습니다."));

        // 2. 이미 다른 계정에 kakaoId가 등록되어 있다면 예외
        if (userRepository.countByKakaoIdManual(kakaoId) > 0) {
            throw new IllegalStateException("이미 등록된 카카오 계정입니다.");
        }

        log.info("✅Extra_info/Service/전달된 kakaoId: {}", kakaoId);
        // 3. kakaoId 저장
        user.setKakaoId(kakaoId);
            return userRepository.save(user);
        }
}

