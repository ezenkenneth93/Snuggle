package com.snuggle.homework.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    /**
     * 매 요청마다 실행되는 JWT 인증 필터
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Authorization 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰이 존재하고 유효한 경우
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 3. JWT에서 phoneNumber 추출 (setSubject로 설정된 값)
            String phoneNumber = jwtTokenProvider.getPhoneNumber(token);

            // 4. phoneNumber로 UserDetails 조회 (DB or 메모리 등에서)
            UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);

            // 5. JWT에서 role 클레임 추출 ("ROLE_USER" 등)
            String role = jwtTokenProvider.getRole(token);

            // 6. Spring Security에서 인식할 수 있는 권한 객체로 변환
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            // 7. 인증 객체 생성 (사용자 정보 + 권한 포함)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,               // 비밀번호는 null로 처리
                            authorities         // 권한 리스트
                    );

            // 8. 인증 객체에 추가 정보 설정 (요청 정보 기반)
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 9. SecurityContext에 인증 객체 저장 (인증 완료 처리)
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 10. 다음 필터 체인 실행
        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 JWT 토큰을 추출하는 유틸 메서드
     * 형식: Authorization: Bearer <token>
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // "Bearer " 문자열 이후부터 자름
        }
        return null;
    }
}
