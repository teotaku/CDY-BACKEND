package com.cdy.cdy.controller;

import com.cdy.cdy.dto.request.FindIdRequestDto;
import com.cdy.cdy.dto.request.LoginRequest;
import com.cdy.cdy.dto.request.SignUpRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.LoginResponse;
import com.cdy.cdy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class JoinController {


    //CommonResponse<T>로 리팩토링

    private final AuthService authService;

    //회원가입

    @Operation(summary = "회원가입", description = "이메일,비밀번호 입력 회원가입")
    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(@RequestBody @Valid
                                                  SignUpRequest signUpRequest) {
        authService.join(signUpRequest);
        return ResponseEntity.ok("회원가입 성공3");
    }

    //로그인

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }


    //로그아웃
    @Operation(summary = "로그아웃", description = "클라이언트쪽에서 서버에서 발급받은 jwt삭제 후 로그아웃처리")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {

        return ResponseEntity.ok("로그아웃 완료. 토큰을 삭제하세요.");
    }

    @Operation(summary = "아이디 찾기", description = "등록된 이름과 이메일 정보로 아이디 찾기(이메일전송)")
    @PostMapping("/find-id")
    public ResponseEntity<String> findUserId(@RequestBody FindIdRequestDto dto) {
        authService.findUserId(dto);
        return ResponseEntity.ok("입력하신 이메일로 아이디를 전송하였습니다.");
    }
}
