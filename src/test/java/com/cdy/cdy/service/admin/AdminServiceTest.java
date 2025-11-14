package com.cdy.cdy.service.admin;

import com.cdy.cdy.dto.request.LoginRequest;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.repository.UserRepository;
import com.cdy.cdy.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {


    @Mock
    UserRepository userRepository;

    @Mock
    AuthService authService;

    @InjectMocks
    AdminService adminService;


    /**
     * 1️⃣ 로그인 시 이메일 없으면 Exception 발생해야 함
     */
    @Test
    void 로그인_이메일없음_예외() {

        // given
        LoginRequest req = new LoginRequest("notfound@test.com", "1234");

        when(userRepository.findByEmail(req.getEmail()))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> adminService.login(req))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 이메일입니다.");
    }

    /**
     * 2️⃣ 로그인 시 ADMIN이 아니면 IllegalArgumentException 발생
     */
    @Test
    void 로그인_관리자아님_예외() {

        // given
        LoginRequest req = new LoginRequest("user@test.com", "1234");

        // 일반 유저(ADMIN 아님)
        User normalUser = User.builder()
                .nickname("normal")
                .email("user@test.com")
                .passwordHash("HASH")
                .role(UserRole.USER)   //  핵심
                .build();

        when(userRepository.findByEmail(req.getEmail()))
                .thenReturn(Optional.of(normalUser));

        // expected
        assertThatThrownBy(() -> adminService.login(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("관리자 계정이 아닙니다.");
    }

    @Test
    void 관리자_로그인_성공() {

        // given
        LoginRequest req = new LoginRequest("admin@test.com", "1234");

        User adminUser = User.builder()
                .nickname("admin")
                .email("admin@test.com")
                .passwordHash("HASH")
                .role(UserRole.ADMIN)   // 🔥 핵심
                .build();

        when(userRepository.findByEmail(req.getEmail()))
                .thenReturn(Optional.of(adminUser));

        // when
        adminService.login(req);

        // then

        verify(userRepository, times(1)).findByEmail(req.getEmail());
        verify(authService, times(1)).login(req);
        verify(userRepository).findByEmail(req.getEmail());
        }

}
