package com.cdy.cdy.dto.response.project;

import com.cdy.cdy.dto.response.MemberBrief;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OneProjectResponse {

    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "프로젝트 제목", example = "알고리즘 스터디")
    private String title;

    @Schema(description = "프로젝트 설명", example = "매주 월요일 문제 풀이 진행")
    private String content;

    @Schema(description = "프로젝트 슬로건", example = "함께 성장하는 팀")
    private String slogan;

    @Schema(description = "팀장 프로필 이미지", example = "user/leader-uuid.png")
    private String leaderImage;

    @Schema(description = "프로젝트 멤버 요약 리스트")
    private List<MemberBrief> memberBriefs;

    @Schema(description = "사용 기술 스택", example = "[\"Spring Boot\", \"React\"]")
    private List<String> techs;
}



