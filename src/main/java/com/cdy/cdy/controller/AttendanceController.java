package com.cdy.cdy.controller;


import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.MonthCalendarResponse;
import com.cdy.cdy.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.ZoneId;


@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @PostMapping("/check")
    public ResponseEntity<Void> checkToday(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getId(); // JWT 인증 후 주입된 사용자 ID
         attendanceService.checkToday(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/calendar")
    public ResponseEntity<MonthCalendarResponse> calendar(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "month",required = false) String month // "YYYY-MM"
    ) {
        YearMonth ym = (month == null || month.isBlank())
                ? YearMonth.now(KST)
                : YearMonth.parse(month);
        return ResponseEntity.ok(attendanceService.getMonth(user.getId(), ym));
    }
}
