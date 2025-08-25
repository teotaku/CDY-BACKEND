package com.cdy.cdy.controller;

import com.cdy.cdy.dto.request.LoginRequest;
import com.cdy.cdy.dto.response.LoginResponse;
import com.cdy.cdy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "", description = "이메일과 비밀번호로 로그인하여 JWT 토큰 발급")
    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok("회원가입 성공13");
    }

    //로그인

    @Operation(summary = "", description = "이메일과 비밀번호로 로그인하여 JWT 토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }

}
