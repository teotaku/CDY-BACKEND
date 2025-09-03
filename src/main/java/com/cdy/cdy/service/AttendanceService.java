package com.cdy.cdy.service;

import com.cdy.cdy.dto.response.CalendarDay;
import com.cdy.cdy.dto.response.MonthCalendarResponse;
import com.cdy.cdy.entity.DailyAttendance;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.repository.AttendanceRepository;
import com.cdy.cdy.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;



    private static final ZoneId KST = ZoneId.of("Asia/Seoul");


    public void checkToday(Long userId) {
        LocalDate today = LocalDate.now(KST);
        if (attendanceRepository.existsByUser_IdAndCheckDate(userId, today)) return;

        try {
            // SELECT 없이 프록시만 (간단/안전)
            User userRef = userRepository.getReferenceById(userId);

            DailyAttendance att = DailyAttendance.check(
                    userRef,
                    today,
                    LocalDateTime.now(KST)
            );
            attendanceRepository.saveAndFlush(att); // 바로 flush해서 UNIQUE 위반 즉시 감지
        } catch (DataIntegrityViolationException ignore) {
            // 동시 클릭/중복요청 → 이미 누가 먼저 저장함 → 무시(멱등)
        }
    }

    /** 2) 월 달력 조회: 날짜별 checked만 넘김 */
    @Transactional(readOnly = true)
    public MonthCalendarResponse getMonth(Long userId, YearMonth ym) {
        // ① 이번 달 범위(1일~말일) 확정
        LocalDate start = ym.atDay(1);        // ex) 2025-09-01
        LocalDate end   = ym.atEndOfMonth();  // ex) 2025-09-30

        // ② DB에서 '이번 달에 찍힌 출석' 전부 조회 (Between = 양 끝 포함)
        List<DailyAttendance> attendancesThisMonth =
                attendanceRepository.findAllByUser_IdAndCheckDateBetween(userId, start, end);

        // ③ 날짜만 뽑아서 Set으로 (O(1) 포함 체크용)
        Set<LocalDate> checkedDates = attendancesThisMonth.stream()
                .map(DailyAttendance::getCheckDate)  // 엔티티 → 날짜
                .collect(Collectors.toSet());

        // (이 아래에서 checkedDates.contains(d) 로 true/false 칠하면 됨)
        List<CalendarDay> days = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            days.add(CalendarDay.builder()
                    .date(d)
                    .checked(checkedDates.contains(d))
                    .build());
        }

        return MonthCalendarResponse.builder()
                .month(ym.toString()) // "YYYY-MM" (DTO가 String month일 때)
                .days(days)
                .build();
    }
    }
