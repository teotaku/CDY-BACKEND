package com.cdy.cdy.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MypageResponse {


    private String nickName;

    private String email;

}
