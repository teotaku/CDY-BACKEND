package com.cdy.cdy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailVerificationResponse {
    private Long id;
    private Long userId;
    private String code;
    private LocalDateTime expiresAt;
    private LocalDateTime verifiedAt;
}