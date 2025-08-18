package com.cdy.cdy.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CheckAttendanceRequest {
    @NotNull @Positive
    private Long userId;

    @NotBlank
    private String status; // PRESENT, ABSENT 등
}
