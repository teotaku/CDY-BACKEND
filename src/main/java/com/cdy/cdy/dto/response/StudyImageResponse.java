package com.cdy.cdy.dto.response;

import com.cdy.cdy.entity.study.StudyImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyImageResponse {
    private String url;  // 실제 접근 가능한 URL (publicUrl or presign)
    private int sortOrder;

    public static StudyImageResponse from(StudyImage img) {
        return StudyImageResponse.builder()
                .sortOrder(img.getSortOrder())
                .build();
    }
}
