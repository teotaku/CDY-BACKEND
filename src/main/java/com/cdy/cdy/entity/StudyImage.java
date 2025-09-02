// src/main/java/com/cdy/cdy/entity/StudyImage.java
package com.cdy.cdy.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_images")

public class StudyImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="study_id", nullable=false)
    private StudyChannel study;

    @Column(name = "object_key", nullable = false, length = 400)
    private String key;               // R2 객체 키 (예: uploads/uuid.jpg)

    @Column(nullable=false)
    private Integer sortOrder;        // 노출 순서

//    @Column(length=200)
//    private String alt;               // 대체 텍스트(선택)

    @Builder
    public StudyImage(StudyChannel study, String key, Integer sortOrder, String alt) {
        this.study = study;
        this.key = key;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
//        this.alt = alt;
    }
}
