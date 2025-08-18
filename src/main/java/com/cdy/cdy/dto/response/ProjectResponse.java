package com.cdy.cdy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private Long createdBy;
    private LocalDateTime createdAt;
}