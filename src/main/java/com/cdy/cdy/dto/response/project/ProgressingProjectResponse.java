package com.cdy.cdy.dto.response.project;

import com.cdy.cdy.dto.response.LeaderInfoProjection;
import com.cdy.cdy.dto.response.MemberBrief;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProgressingProjectResponse {

    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "총 모집 정원", example = "5")
    private long capacity;

    @Schema(description = "프로젝트 제목", example = "쇼핑몰 클론 프로젝트")
    private String title;

    @Schema(description = "현재 참여 중인 인원 수", example = "4")
    private long memberCount;

    private List<String> techs;

    @Schema(description = "모집 포지션", example = "[\"백엔드\", \"프론트엔드\"]")
    private List<String> position;

    @Schema(description = "프로젝트 카카오톡 링크", example = "https://open.kakao.com/o/xxxx")
    private String kakaoLink;

    @Schema(description = "프로젝트 멤버 요약 리스트")
    private List<MemberBrief> memberBriefs;

    @Schema(description = "대표 이미지 Key", example = "project/clone-shop.png")
    private String imageKey;

    @Schema(description = "프로젝트 팀장 정보 (id,imageURL)")
    private LeaderInfoProjection leaderInfoProjection;

    @Schema(description = "프로젝트 완료 누른 사람 명수", example = "4")
    private long complicatedCount;

    @Schema(description = "현재 로그인한 유저의 status 상태")
    private String currentUserStatus;
}
