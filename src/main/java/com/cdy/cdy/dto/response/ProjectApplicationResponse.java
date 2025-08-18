package com.cdy.cdy.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectApplicationResponse {
    private Long id;
    private Long projectId;
    private Long applicantId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private Long reviewerId;
}