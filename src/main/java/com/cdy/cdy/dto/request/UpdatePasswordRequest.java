// dto/mypage/UpdatePasswordRequest.java
package com.cdy.cdy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank(message = "현재 비밀번호가 필요합니다.")
        String currentPassword,

        @NotBlank(message = "새 비밀번호가 필요합니다.")
        @Size(min = 8, max = 100, message = "비밀번호는 8~100자 입니다.")
        String newPassword
) { }
