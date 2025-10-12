// src/main/java/com/cdy/cdy/entity/ProjectMember.java
package com.cdy.cdy.entity.project;

import com.cdy.cdy.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProjectMemberStatus status;

    private List<String> techs;

    private String position;


    @Enumerated(EnumType.STRING) @Column(length = 30)
    private ProjectMemberRole role; // 역할(LEADER/DEV/...)

    @Column(name = "joined_at")
    private LocalDateTime joinedAt; // 합류 시각


    @Transactional
    public void updatePosition(String position) {
        if (position == null || position.isBlank()) {
            this.position = "포지션없음";
        } else {
            this.position = position;
        }
    }

    @Transactional
    public void updateTechs(List<String> techs) {
        if (techs == null || techs.isEmpty()) {
            this.techs = List.of("기술 없음");
        } else {
            this.techs = techs;
        }
    }


    @Transactional
    public void approve() {
        this.status = ProjectMemberStatus.APPROVED;
        this.joinedAt = LocalDateTime.now();
    }

    @Transactional
    public void reject() {
        this.status = ProjectMemberStatus.REJECTED;
    }

    @Transactional
    public void cancel() {
        this.status = ProjectMemberStatus.CANCEL;
    }

    @Transactional
    public void complete() {
        this.status = ProjectMemberStatus.COMPLICATED;
    }
    @Transactional
    public boolean isCompleted() {
        return this.status == ProjectMemberStatus.COMPLICATED;
    }

    @Transactional
    public void changeStatus(ProjectMemberStatus status) {
        this.status = status;
    }
    @Transactional
    public void changeJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

}
