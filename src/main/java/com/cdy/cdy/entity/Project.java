package com.cdy.cdy.entity;

import com.cdy.cdy.dto.request.CreateProjectRequest;
import com.cdy.cdy.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "projects")
public class Project  {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Lob
    private String description;

    private List<String> techs;

    private Integer capacity;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> projectMembers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    private String logoImageUrl; // null 허용 (이미지 나중에 붙일 수 있음)

    private String kakaoLink;

    @Builder
    private Project(String title, String description, Integer capacity, User manager, String logoImageUrl,String kakaoLink) {
        this.title = Objects.requireNonNull(title);
        this.description = description;
        this.capacity = capacity;
        this.manager = Objects.requireNonNull(manager);
        this.logoImageUrl = logoImageUrl;
        this.kakaoLink = kakaoLink;
    }

    // 👉 DTO에서 바로 변환할 수 있게 팩토리 메서드
    public static Project from(CreateProjectRequest req, User leader) {
        return Project.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .capacity(req.getCapacity())
                .manager(leader)
                .logoImageUrl(null) // null 가능
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
}
