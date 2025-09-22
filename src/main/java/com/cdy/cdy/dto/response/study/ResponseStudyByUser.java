package com.cdy.cdy.dto.response.study;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseStudyByUser {

    private Long studyId;
    private String content;
}
