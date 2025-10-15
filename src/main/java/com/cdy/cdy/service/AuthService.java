package com.cdy.cdy.service;

import com.cdy.cdy.dto.request.FindIdRequestDto;
import com.cdy.cdy.dto.request.LoginRequest;
import com.cdy.cdy.dto.request.SignUpRequest;
import com.cdy.cdy.dto.response.LoginResponse;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserCategory;
import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.jwt.JWTUtil;
import com.cdy.cdy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final MailService mailService;

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
                .name(signUpRequest.getName())
                .passwordHash(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(UserRole.USER) // 필요 시 지정, 아니면 of()로 기본값 처리
                .phoneNumber(signUpRequest.getPhoneNumber())
                .category(UserCategory.from(signUpRequest.getUserCategory()))
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

        String role = (user.getRole() == null) ? UserRole.USER.name() : user.getRole().name();


        String access = jwtUtil.createJwt(user.getId(), user.getEmail(), role, accessExpireMs);
//        String refresh = jwtUtil.createRefreshToken(user.getId(), user.getRole().toString());


        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .accessToken(access)
                .build();
    }

    @Transactional(readOnly = true)
    public void findUserId(FindIdRequestDto dto) {

        User user = userRepository.findByNameAndEmail(dto.getName(), dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));

        // 2️⃣ 아이디 일부 마스킹
//        String maskedId = maskUserId(user.getEmail());

        // 3️⃣ 메일 전송
        mailService.sendMail(
                dto.getEmail(),
                "[CDY] 아이디 찾기 안내",
                "회원님의 아이디는 다음과 같습니다.\n\n" + user.getEmail()
        );
    }


    @Transactional
    public void findPassword(FindIdRequestDto dto) {

        User user = userRepository.findByNameAndEmail(dto.getName(), dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));


        String tempPassword = createTempPassword();


        mailService.sendMail(user.getEmail(),
                "[CDY] 임시 비밀번호 안내",
                "회원님의 임시 비밀번호는 다음과 같습니다. \n\n " + tempPassword);
    }


    // 아이디 일부 마스킹 (보안용)
    private String maskUserId(String userEmail) {
        // 1️⃣ null 체크 (혹시 모를 NPE 방지)
        if (userEmail == null || !userEmail.contains("@")) {
            return userEmail;
        }

        // 2️⃣ 이메일을 @ 기준으로 앞/뒤 분리
        String[] parts = userEmail.split("@");
        String localPart = parts[0];  // 아이디 부분 (dongik9467)
        String domainPart = parts[1]; // 도메인 부분 (naver.com)

        // 3️⃣ 아이디 부분 마스킹 로직
        if (localPart.length() <= 2) {
            localPart = localPart.charAt(0) + "*";
        } else {
            int front = 3; // 앞부분 보여줄 글자 수
            int back = Math.min(2, localPart.length() - front); // 뒤 부분 보여줄 글자 수
            String visibleFront = localPart.substring(0, front);
            String visibleBack = localPart.substring(localPart.length() - back);
            String stars = "*".repeat(localPart.length() - (front + back));
            localPart = visibleFront + stars + visibleBack;
        }

        // 4️⃣ 다시 조합해서 반환
        return localPart + "@" + domainPart;
    }

    private String createTempPassword() {
        // 사용할 문자 조합
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // 암호학적으로 안전한 난수 생성기
        SecureRandom random = new SecureRandom();

        // 결과 문자열 생성
        StringBuilder sb = new StringBuilder();

        // 10자리 비밀번호 생성
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(chars.length()); // 0~chars.length()-1 범위 랜덤
            sb.append(chars.charAt(index)); // 해당 인덱스의 문자 추가
        }

        return sb.toString();

}}