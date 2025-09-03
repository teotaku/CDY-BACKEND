package com.cdy.cdy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.util.List;

@Getter @Builder
@AllArgsConstructor
public class MonthCalendarResponse {
    private String month;
    private List<CalendarDay> days;
}