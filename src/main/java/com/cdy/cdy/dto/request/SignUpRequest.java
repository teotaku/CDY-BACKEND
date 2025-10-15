package com.cdy.cdy.dto.request;

import com.cdy.cdy.entity.UserCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SignUpRequest {

    @Schema(description = "이메일 주소", example = "teo@example.com")
    private String email;

    @Schema(description = "유저이름", example = "김철수")
    private String name;

    @Schema(description = "비밀번호", example = "1234abcdA!")
    private String password;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "사용자 카테고리 (CODING / DESIGN / VIDEO_EDITING 중 하나)",
            example = "CODING")
    private String userCategory;

    @Schema(description = "닉네임", example = "테오")
    private String nickname;
}