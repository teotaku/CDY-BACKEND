// src/main/java/com/cdy/cdy/entity/Project.java
package com.cdy.cdy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "projects")
public class Project extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name; // 프로젝트 이름

    @Column(length = 200)
    private String slogan; // 슬로건

    @Lob
    private String description; // 긴 설명

    @Column(name = "description_image_url", length = 500)
    private String descriptionImageUrl; // 설명을 이미지로 대체 가능

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 프로젝트:N - 팀장(유저):1
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager; // 팀장

    @Column(name = "logo_image_url", length = 500)
    private String logoImageUrl; // 대표 로고 이미지 URL

    public static Project create(String name, User manager, String slogan, String description, String descriptionImageUrl, String logoImageUrl) {
        Objects.requireNonNull(name); Objects.requireNonNull(manager);
        var p = new Project();
        p.name = name; p.manager = manager; p.slogan = slogan; p.description = description;
        p.descriptionImageUrl = descriptionImageUrl; p.logoImageUrl = logoImageUrl;
        return p;
    }

    public void changeManager(User newManager) { this.manager = Objects.requireNonNull(newManager); }

    public void updateOverview(String slogan, String description, String descriptionImageUrl, String logoImageUrl) {
        this.slogan = slogan; this.description = description; this.descriptionImageUrl = descriptionImageUrl; this.logoImageUrl = logoImageUrl;
    }
}
