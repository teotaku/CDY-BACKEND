// src/main/java/com/cdy/cdy/entity/ProjectMember.java
package com.cdy.cdy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "project_members",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_project_member_unique",
                columnNames = {"project_id", "user_id"} // 같은 프로젝트에 같은 유저는 1회만 가입
        )
)
public class ProjectMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 멤버레코드:N - 프로젝트:1
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 멤버레코드:N - 유저:1
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING) @Column(length = 30)
    private ProjectMemberRole role; // 역할(LEADER/DEV/...)

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt; // 합류 시각

    public static ProjectMember join(Project project, User user, ProjectMemberRole role,
                                     LocalDateTime joinedAt) {
        Objects.requireNonNull(project); Objects.requireNonNull(user);
        var pm = new ProjectMember();
        pm.project = project; // ← 공개 세터 대신 생성 루트에서 필수값 주입
        pm.user = user;
        pm.role = (role == null ? ProjectMemberRole.DEV : role);
        pm.joinedAt = (joinedAt == null ? LocalDateTime.now() : joinedAt);
        return pm;
    }

    public void changeRole(ProjectMemberRole newRole) { this.role = Objects.requireNonNull(newRole); }
}
