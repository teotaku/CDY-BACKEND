package com.cdy.cdy.dto.request;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApplyProjectRequest {

    private Long projectId; // 신청할 프로젝트 ID

    private String position; // 지원자가 선택한 포지션

    private List<String> techs; // 지원자가 적은 기술 (혹은 선택)

    private List<AnswerDto> answers; // 질문에 대한 답변들

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class AnswerDto {
        private Long questionId; // 어떤 질문에 대한 답인지
        private String answer;   // 답변 텍스트
    }
}