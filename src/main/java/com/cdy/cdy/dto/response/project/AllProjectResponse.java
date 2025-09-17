package com.cdy.cdy.dto.response.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AllProjectResponse {
    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;
    @Schema(description = "프로젝트 슬로건", example = "매일매일 성장하는 스터디")
    private String slogan;
    @Schema(description = "프로젝트 제목", example = "알고리즘 스터디")
    private String title;

    @Schema(description = "대표 이미지 Key (S3 등)", example = "project/uuid-image.png")
    private String imageKey;

    @Schema(description = "생성일시", example = "2025-09-17T12:00:00")
    private LocalDateTime createdAt;
}


