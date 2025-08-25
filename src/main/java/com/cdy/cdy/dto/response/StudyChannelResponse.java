package com.cdy.cdy.dto.response;

import com.cdy.cdy.entity.StudyChannel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyChannelResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public static StudyChannelResponse from(StudyChannel study) {
        return StudyChannelResponse.builder()
                .id(study.getId())
                .content(study.getContent())
                .createdAt(study.getCreatedAt()) // BaseEntity 상속받아서 자동 기록
                .build();
    }
}