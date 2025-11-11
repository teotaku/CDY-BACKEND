package com.cdy.cdy.dto.response.study;

import com.cdy.cdy.dto.response.StudyImageResponse;
import com.cdy.cdy.entity.study.StudyChannel;
import com.cdy.cdy.entity.study.StudyImage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyChannelResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private List<StudyImageResponse> images;

    public static StudyChannelResponse from(StudyChannel study,List<StudyImageResponse> images) {
        return StudyChannelResponse.builder()
                .id(study.getId())
                .content(study.getContent())
                .createdAt(study.getCreatedAt())
                .images(images)// BaseEntity 상속받아서 자동 기록
                .build();
    }
    public static StudyChannelResponse fromWithImage(StudyChannel study, List<StudyImage> images) {
        return from(study, List.of()); // 이미지가 없을 땐 빈 리스트로
    }
}