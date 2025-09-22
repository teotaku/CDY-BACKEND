package com.cdy.cdy.dto.response.project;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectApplicationResponse {

    @Schema(description = "신청 ID", example = "10")
    private Long id;

    @Schema(description = "프로젝트 ID", example = "1")
    private Long projectId;

    @Schema(description = "유저 ID", example = "100")
    private Long userId;

    @Schema(description = "신청자 닉네임", example = "teo")
    private String nickName;

    @Schema(description = "프로필 이미지", example = "user/teo.png")
    private String profileImage;

    @Schema(description = "신청자가 작성한 답변 리스트")
    private List<AnswerResponseDTO> answers;

    @Schema(description = "신청자 기술")
    private String techs;

    @Schema(description = "신청자 포지션")
    private String position;


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    @Schema(description = "프로젝트 신청 답변 DTO")
    public static class AnswerResponseDTO {

        @Schema(description = "질문 ID", example = "5")
        private Long questionId;

        @Schema(description = "질문 내용", example = "자기소개를 해주세요")
        private String questions;

        @Schema(description = "답변 내용", example = "저는 백엔드 개발자 지망생입니다")
        private String answer;
    }
    }

