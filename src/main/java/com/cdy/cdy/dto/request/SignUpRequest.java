package com.cdy.cdy.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SignUpRequest {


    private String email;

    private String password;

    private String nickname;
}