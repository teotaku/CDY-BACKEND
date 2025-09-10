package com.cdy.cdy.jwt;

import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // ✅ Swagger 요청은 필터 스킵
        if (uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger-ui") ||
                uri.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return; // ✅ 추가
        }


        //request에서 Authorization 헤더를 찾음.
        String authorization = request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("token null");
            filterChain.doFilter(request, response);
            return ;
            // null 이거나 bearer로 시작하지않으면 종료하면서 리턴해버리기
        }
        // ✅ split 안전 처리
        String[] parts = authorization.split(" ");
        if (parts.length < 2) {
            filterChain.doFilter(request, response);
            return; // ✅ 추가
        }

        String token = parts[1]; // ✅ 기존 코드 수정

        Boolean expired = jwtUtil.isExpired(token);
        if (expired) {
            //토큰 소멸시간 검증
            System.out.println("token expired");
            filterChain.doFilter(request,response);
            return;
        }
        Long userId = jwtUtil.getUserId(token);
        String email = jwtUtil.getUsername(token);
        String roleStr = jwtUtil.getRole(token);
        final UserRole userRole = (roleStr != null) ? UserRole.valueOf(roleStr) : UserRole.USER;


        User user = User.builder()
                .id(userId)
                .email(email)
                .passwordHash("temppassword")
                .role(userRole)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken
                (customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록

        SecurityContextHolder.getContext().setAuthentication(authToken);


        filterChain.doFilter(request, response);


    }
}
