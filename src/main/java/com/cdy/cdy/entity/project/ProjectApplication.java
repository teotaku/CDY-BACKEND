// src/main/java/com/cdy/cdy/entity/ProjectApplication.java
package com.cdy.cdy.entity.project;

import com.cdy.cdy.entity.ApplicationsStatus;
import com.cdy.cdy.entity.BaseEntity;
import com.cdy.cdy.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "project_applications")
public class ProjectApplication extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 신청:N - 프로젝트:1
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 신청:N - 유저(신청자):1
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @Column(length = 30)
    private String position; // 희망 포지션(문자열 or enum)

    @Enumerated(EnumType.STRING) @Column(length = 20, nullable = false)
    private ApplicationsStatus status = ApplicationsStatus.PENDING;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt; // 신청 시각

    @Column(name = "decided_at")
    private LocalDateTime decidedAt; // 결정 시각(전엔 null)

    public static ProjectApplication apply(Project project, User applicant, String position, LocalDateTime now) {
        Objects.requireNonNull(project); Objects.requireNonNull(applicant);
        var pa = new ProjectApplication();
        pa.project = project; pa.applicant = applicant;
        pa.position = position; pa.status = ApplicationsStatus.PENDING;
        pa.appliedAt = (now == null ? LocalDateTime.now() : now);
        return pa;
    }

    public void approve(LocalDateTime when) {
        ensurePending();
        this.status = ApplicationsStatus.APPROVED;
        this.decidedAt = (when == null ? LocalDateTime.now() : when);
    }

    public void reject(LocalDateTime when) {
        ensurePending();
        this.status = ApplicationsStatus.REJECTED;
        this.decidedAt = (when == null ? LocalDateTime.now() : when);
    }

    private void ensurePending() {
        if (this.status != ApplicationsStatus.PENDING) throw new IllegalStateException("이미 결정됨: " + status);
    }
}
