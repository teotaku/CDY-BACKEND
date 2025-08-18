package com.cdy.cdy.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectAnswerResponse {
    private Long id;
    private Long questionId;
    private String content;
    private Long createdBy;
    private LocalDateTime createdAt;
}