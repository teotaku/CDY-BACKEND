package com.cdy.cdy.dto.response.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectQuestionResponse {
    @Schema(description = "질문 ID", example = "10")
    private Long id;
    @Schema(description = "해당 질문이 속한 프로젝트 ID", example = "3")
    private Long projectId;
    @Schema(description = "질문 내용", example = "이 프로젝트를 지원하게 된 동기는 무엇인가요?")
    private String content;

}
