package com.cdy.cdy.service;

import com.cdy.cdy.entity.User;
import com.cdy.cdy.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 이미 SecurityConfig에 Bean 등록돼 있죠

    @Transactional
    public void changeNickname(Long userId, String newNickname) {
        User u = userRepository.findById(userId).orElseThrow();
        if (userRepository.existsByNicknameAndIdNot(newNickname, userId)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임");
        }
        u.changeNickname(newNickname); // 엔티티에 이미 메서드 있음
    }

    @Transactional
    public void changeEmail(Long userId, String newEmail) {
        User u = userRepository.findById(userId).orElseThrow();
        if (userRepository.existsByEmailAndIdNot(newEmail, userId)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일");
        }
        u.changeEmail(newEmail); // 없으면 동일 패턴으로 추가
        // 이메일을 username으로 쓰는 프로젝트라면: 토큰 재발급/재로그인 권장
    }

    @Transactional
    public void changePassword(Long userId, String currentPw, String newPw) {
        User u = userRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(currentPw, u.getPasswordHash())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않음");
        }
        u.changePasswordHash(passwordEncoder.encode(newPw)); // 엔티티 메서드 활용
    }
}
