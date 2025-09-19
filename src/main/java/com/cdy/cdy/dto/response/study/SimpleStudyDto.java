package com.cdy.cdy.dto.response.study;

import com.cdy.cdy.entity.UserCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SimpleStudyDto {
    private Long studyId;
    private Long userId;
    private String userImage;
    private UserCategory category;

}