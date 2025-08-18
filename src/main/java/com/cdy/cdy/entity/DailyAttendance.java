// src/main/java/com/cdy/cdy/entity/DailyAttendance.java
package com.cdy.cdy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "daily_attendance",
        uniqueConstraints = @UniqueConstraint( // ← 복합 유니크(조합 유일) 제약
                name = "uk_attendance_user_date",
                columnNames = {"user_id", "check_date"} // 같은 유저가 같은 날짜에 '1번만' 존재
        )
)
public class DailyAttendance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 출석:N - 유저:1
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate; // 날짜만(타임존 영향 최소화, 1일 1회 규칙 판단에 적합)

    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt; // 실제 체크 시각(감사/정렬용)

    public static DailyAttendance check(User user, LocalDate date, LocalDateTime now) {
        Objects.requireNonNull(user); Objects.requireNonNull(date);
        var d = new DailyAttendance();
        d.user = user; d.checkDate = date; d.checkedAt = (now == null ? LocalDateTime.now() : now);
        return d;
    }
}
