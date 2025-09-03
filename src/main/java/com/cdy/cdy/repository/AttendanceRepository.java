package com.cdy.cdy.repository;

import com.cdy.cdy.entity.DailyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<DailyAttendance ,Long> {

    boolean existsByUser_IdAndCheckDate(Long userId, LocalDate checkDate);
    // (UserId 라고 붙여도 동작: existsByUserIdAndCheckDate)

    // 한 달 범위 조회 (양끝 포함)
    List<DailyAttendance> findAllByUser_IdAndCheckDateBetween(Long userId, LocalDate start, LocalDate end);
}
