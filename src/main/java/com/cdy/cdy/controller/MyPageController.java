package com.cdy.cdy.controller;

import com.cdy.cdy.dto.request.UpdateEmailRequest;
import com.cdy.cdy.dto.request.UpdateNicknameRequest;
import com.cdy.cdy.dto.request.UpdatePasswordRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    // 닉네임 변경
    @PatchMapping("/nickname")
    public ResponseEntity<Void> changeNickname(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody UpdateNicknameRequest req) {
        myPageService.changeNickname(principal.getId(), req.nickname());
        return ResponseEntity.noContent().build(); // 204
    }

    // 이메일 변경
    @PatchMapping("/email")
    public ResponseEntity<String> changeEmail(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody UpdateEmailRequest req) {
        myPageService.changeEmail(principal.getId(), req.email());
        // 이메일이 JWT의 subject라면 프론트에서 재로그인 처리하도록 메시지 반환
        return ResponseEntity.ok("이메일 변경 완료. 다시 로그인 해주세요.");
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody UpdatePasswordRequest req) {
        myPageService.changePassword(principal.getId(), req.currentPassword(), req.newPassword());
        return ResponseEntity.ok("비밀번호 변경 완료. 다시 로그인 해주세요.");
    }
}
