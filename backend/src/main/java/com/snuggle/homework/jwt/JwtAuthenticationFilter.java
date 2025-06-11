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

import com.snuggle.homework.jwt.AdminDetailsService;
import com.snuggle.homework.jwt.CustomUserDetailsService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final AdminDetailsService adminDetailsService;

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

            // 3. 토큰에서 role 클레임 추출
            String role = jwtTokenProvider.getRole(token);  // "ROLE_USER" or "ROLE_ADMIN"
            UserDetails userDetails;

            if ("ROLE_ADMIN".equals(role)) {
                // 관리자: subject는 username
                String username = jwtTokenProvider.getUsername(token);
                userDetails = adminDetailsService.loadUserByUsername(username);
            } else {
                // 일반 사용자: subject는 phoneNumber
                String phone = jwtTokenProvider.getPhoneNumber(token);
                userDetails = userDetailsService.loadUserByUsername(phone);
            }

            // 4. 권한 객체 생성
            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(role)
            );

            // 5. 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );

            // 6. 추가 정보 설정
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 7. SecurityContext에 인증 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 8. 다음 필터 체인 실행
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
