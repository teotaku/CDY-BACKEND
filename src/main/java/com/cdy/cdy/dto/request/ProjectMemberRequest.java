package com.cdy.cdy.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectMemberRequest {
    @NotNull @Positive
    private Long projectId;

    @NotNull @Positive
    private Long userId;

    @NotNull @Positive
    private Long roleId;
}