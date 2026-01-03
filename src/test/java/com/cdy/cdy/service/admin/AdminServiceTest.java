package com.cdy.cdy.service.admin;

import com.cdy.cdy.dto.request.LoginRequest;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.repository.UserRepository;
import com.cdy.cdy.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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


    @Test
    void 유저아이디받고_해당유저_삭제() {


        //gvien

        User user = User.builder()
                .id(1L)
                .name("가나다")
                .email("asdf@naver.com")
                .passwordHash("12345125")
                .build();

        given(userRepository.findById(user.getId()))
                .willReturn(Optional.of(user));


        //when

        adminService.deleteUser(user.getId());
        //then
        assertThat(user.getEmail()).isEqualTo(null);


        }

    @Test
    void 유저삭제시_존재하지않는_유저면_에러발생() {


        //given

        Long id = 1L;

        given(userRepository.findById(id))
                .willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> adminService.deleteUser(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("해당 아이디 유저가 존재하지 않습니다. id: 1");

    }
}
