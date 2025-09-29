package com.cdy.cdy.controller;

import com.cdy.cdy.dto.request.SignUpRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;


    @PreAuthorize("hasRole('ADMIN)")
    @Operation(summary = "회원가입 admin용")
    @PostMapping("/create")
    public ResponseEntity<String> createUser(SignUpRequest signUpRequest,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.join(signUpRequest);
        return ResponseEntity.ok("회원가입 완료");
    }


}
