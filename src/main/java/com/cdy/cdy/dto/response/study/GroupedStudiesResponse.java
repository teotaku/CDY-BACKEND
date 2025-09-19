package com.cdy.cdy.dto.response.study;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class GroupedStudiesResponse {
    private Page<SimpleStudyDto> coding;
    private Page<SimpleStudyDto> design;
    private Page<SimpleStudyDto> video;
}
