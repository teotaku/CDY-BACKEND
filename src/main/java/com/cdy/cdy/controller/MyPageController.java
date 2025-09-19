package com.cdy.cdy.controller;

import com.cdy.cdy.dto.request.UpdateMyImage;
import com.cdy.cdy.dto.request.mypage.UpdateEmailRequest;
import com.cdy.cdy.dto.request.mypage.UpdateNicknameRequest;
import com.cdy.cdy.dto.request.mypage.UpdatePasswordRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.MypageResponse;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.repository.UserRepository;
import com.cdy.cdy.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final UserRepository userRepository;


    //마이 페이지 조회
    @GetMapping
    public ResponseEntity<MypageResponse> getMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getEmail();
        Long id = userDetails.getId();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("아이디를 찾을수없습니다."));

        String nickname = user.getNickname();

        MypageResponse mypageResponse = MypageResponse.builder()
                .nickName(nickname)
                .email(email)
                .build();
        return ResponseEntity.ok(mypageResponse);


    }

    // 닉네임 변경
    @Operation(summary = "닉네임 변경", description = "현재 로그인한 사용자의 닉네임을 변경합니다.")
    @PatchMapping("/nickname")
    public ResponseEntity<Void> changeNickname(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody UpdateNicknameRequest req) {
        myPageService.changeNickname(principal.getId(), req.nickname());
        return ResponseEntity.noContent().build(); // 204
    }

    // 이메일 변경

    @Operation(summary = "이메일 변경", description = "현재 로그인한 사용자의 이메일을 변경합니다. (JWT subject가 이메일일 경우, 재로그인 필요)")
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
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다. 변경 후 재로그인이 필요합니다.")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody UpdatePasswordRequest req) {
        myPageService.changePassword(principal.getId(), req.currentPassword(), req.newPassword());
        return ResponseEntity.ok("비밀번호 변경 완료. 다시 로그인 해주세요.");
    }

    //이미지 변경
    @PatchMapping("/image")
    @Operation(summary = "이미지 변경", description = "로그인한 사용자의 이미지변경")
    public ResponseEntity<String> changeImage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @RequestBody UpdateMyImage updateMyImage) {
        myPageService.changeImage(userDetails.getId(), updateMyImage);
     return ResponseEntity.ok("이미지가 변경되었습니다.");
    }
}
