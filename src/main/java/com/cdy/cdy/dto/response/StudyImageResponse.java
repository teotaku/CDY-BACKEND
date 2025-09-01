package com.cdy.cdy.dto.response;

import com.cdy.cdy.entity.StudyImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyImageResponse {
    private String key;  // 저장된 키
    private String url;  // 실제 접근 가능한 URL (publicUrl or presign)
    private int sortOrder;

    public static StudyImageResponse from(StudyImage img, String url) {
        return StudyImageResponse.builder()
                .key(img.getKey())
                .url(url)
                .sortOrder(img.getSortOrder())
                .build();
    }
}
