// dto/mypage/UpdateNicknameRequest.java
package com.cdy.cdy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNicknameRequest(
        @NotBlank(message = "닉네임은 비어 있을 수 없습니다.")
        @Size(min = 2, max = 50, message = "닉네임은 2~50자 입니다.")
        String nickname
) { }
