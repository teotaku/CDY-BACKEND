package com.cdy.cdy.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberBrief {

    private Long userId;
    private String name;
    private String profileUrl;
}
