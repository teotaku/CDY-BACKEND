// dto/mypage/UpdateEmailRequest.java
package com.cdy.cdy.dto.request.mypage;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmailRequest(
        @NotBlank(message = "이메일은 비어 있을 수 없습니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 255, message = "이메일은 최대 255자 입니다.")
        String email
) { }
