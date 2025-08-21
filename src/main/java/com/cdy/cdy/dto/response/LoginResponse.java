package com.cdy.cdy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {


    private Long userId;
    private String email;
    private String accessToken;
}
