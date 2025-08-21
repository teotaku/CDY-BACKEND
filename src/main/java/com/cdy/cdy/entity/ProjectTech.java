// src/main/java/com/cdy/cdy/entity/ProjectTech.java
package com.cdy.cdy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "project_techs",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_project_tech",
                columnNames = {"project_id", "tech_tag_id"} // 동일 프로젝트-태그 중복 연결 방지
        )
)
public class ProjectTech {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 연결:N - 프로젝트:1
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 연결:N - 태그:1
    @JoinColumn(name = "tech_tag_id", nullable = false)
    private TechTag techTag;

    public static ProjectTech link(Project project, TechTag techTag) {
        Objects.requireNonNull(project); Objects.requireNonNull(techTag);
        var pt = new ProjectTech();
        pt.project = project; pt.techTag = techTag;
        return pt;
    }

    @Builder
    public ProjectTech(Project project, String techName) {
        this.project = project;
        this.techName = techName;
    }
}
