package com.cdy.cdy.dto.response.project;

import com.cdy.cdy.dto.response.MemberBrief;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApplyingProjectResponse {

    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "프로젝트 제목", example = "AI 스터디")
    private String title;

    @Schema(description = "현재 신청한 인원 수", example = "3")
    private long memberCount;

    @Schema(description = "모집 포지션", example = "[\"백엔드\", \"프론트엔드\"]")
    private List<String> position;

    @Schema(description = "멤버 요약 리스트")
    private List<MemberBrief> memberBriefs;

    @Schema(description = "대표 이미지 Key", example = "project/ai-study.png")
    private String imageKey;

    @Schema(description = "기술 스택", example = "[\"Java\", \"Spring\"]")
    private List<String> techs;


    @Schema(description = "프로젝트 정원", example = "4")
    private Integer capacity;
}



