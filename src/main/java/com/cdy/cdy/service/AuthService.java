package com.cdy.cdy.service;

import com.cdy.cdy.dto.request.LoginRequest;
import com.cdy.cdy.dto.request.SignUpRequest;
import com.cdy.cdy.dto.response.LoginResponse;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.jwt.JWTUtil;
import com.cdy.cdy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Value("${jwt.expiration}")
    private long accessExpireMs;

    @Transactional
    public void join(SignUpRequest signUpRequest) {

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .nickname(signUpRequest.getNickname())
                .email(signUpRequest.getEmail())
                .passwordHash(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(UserRole.USER) // 필요 시 지정, 아니면 of()로 기본값 처리
                .build();
        userRepository.save(user);

    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail
                (request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 이메일 또는 비밀번호"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("잘못된 이메일 또는 비밀번호");
        }

        String role = (user.getRole() == null) ? null : user.getRole().name();



        String access = jwtUtil.createJwt(user.getEmail(), null,accessExpireMs);
//        String refresh = jwtUtil.createRefreshToken(user.getId(), user.getRole().toString());


        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .accessToken(access)
                .build();
    }

}
