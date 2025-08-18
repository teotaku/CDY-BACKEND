package com.cdy.cdy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyAttendanceResponse {
    private Long id;
    private Long userId;
    private LocalDate date;
    private String status;
    private LocalDateTime checkedAt;
}