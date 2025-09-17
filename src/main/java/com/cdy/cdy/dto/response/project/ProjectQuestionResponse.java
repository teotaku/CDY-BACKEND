package com.cdy.cdy.dto.response.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectQuestionResponse {
    private Long id;
    private Long projectId;
    private String content;
    private Long createdBy;
    private LocalDateTime createdAt;
}
