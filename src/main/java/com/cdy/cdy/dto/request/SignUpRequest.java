package com.cdy.cdy.dto.request;

import com.cdy.cdy.entity.UserCategory;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SignUpRequest {


    private String email;

    private String password;

    private String phoneNumber;

    private String userCategory;

    private String nickname;
}