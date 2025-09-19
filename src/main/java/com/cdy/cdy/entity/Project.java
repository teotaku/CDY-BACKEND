package com.cdy.cdy.entity;

import com.cdy.cdy.dto.request.CreateProjectRequest;
import com.cdy.cdy.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "projects")
public class Project extends BaseEntity  {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Lob
    private String description;


    @ElementCollection
    @CollectionTable(name = "project_tech_names",
            joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tech_name")
    private List<String> techs;

    @ElementCollection
    @CollectionTable(name = "project_positions",
            joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "position")
    private List<String> positions;

    @Builder.Default
    private Integer capacity = 0;

    private String slogan;

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> projectMembers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    private String logoImageKey; // null 허용 (이미지 나중에 붙일 수 있음)

    private String kakaoLink;


    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.IN_PROGRESS;




    // 👉 DTO에서 바로 변환할 수 있게 팩토리 메서드
    public static Project from(CreateProjectRequest req, User leader) {
        return Project.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .capacity(req.getCapacity())
                .manager(leader)
                .logoImageKey(null) // null 가능
                .kakaoLink(req.getKakaoLink())
                .build();
    }

    // ✅ 현재 참여 인원 수 계산 메서드
    public int getMemberCount() {
        return projectMembers.size();
    }

    // ✅ 매니저(팀장) 연락처 반환
    public String getContact() {
        return manager.getPhoneNumber(); // User 엔티티에 phone 필드 필요
    }

    public void complete() {
        if (this.status == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 프로젝트입니다.");
        }
        this.status = ProjectStatus.COMPLETED;
    }
}
