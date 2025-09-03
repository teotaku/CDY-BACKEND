package com.cdy.cdy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
public class CalendarDay {
    private LocalDate date;
    private boolean checked; // 출석 여부
}