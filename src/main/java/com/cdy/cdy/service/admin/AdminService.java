package com.cdy.cdy.service.admin;


import com.cdy.cdy.dto.admin.AdminHomeResponseDto;
import com.cdy.cdy.dto.admin.CursorResponse;
import com.cdy.cdy.dto.request.SignUpRequest;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserCategory;
import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createdAdmin(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .nickname(signUpRequest.getNickname())
                .email(signUpRequest.getEmail())
                .name(signUpRequest.getName())
                .passwordHash(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(UserRole.ADMIN) // 필요 시 지정, 아니면 of()로 기본값 처리
                .phoneNumber(signUpRequest.getPhoneNumber())
                .category(UserCategory.from(signUpRequest.getUserCategory()))
                .build();
        userRepository.save(user);
    }


    public CursorResponse<AdminHomeResponseDto> getHomeData(Long lastUserId, int limit) {

        List<AdminHomeResponseDto> data = userRepository.findHomeData(lastUserId, limit);

        Long nextCursor = data.isEmpty() ? null : data.get(data.size() - 1).getId();
        boolean hasNext = data.size() == limit;

        return new CursorResponse<>(data, nextCursor, hasNext);
    }
}
