// src/main/java/com/cdy/cdy/entity/StudyChannel.java
package com.cdy.cdy.entity;

import com.cdy.cdy.dto.request.CreateStudyChannelRequest;
import com.cdy.cdy.dto.request.UpdateStudyChannelRequest;
import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_channels")
public class StudyChannel extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 글:N - 유저:1
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // 작성자

    @Column
    private String category; // 카테고리



    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl; // 썸네일 이미지 URL(S3 등 외부 저장)

    // ✅ Builder 생성자
    @Builder
    private StudyChannel(User owner, String category,  String content, String thumbnailUrl) {
        this.owner = Objects.requireNonNull(owner);
        this.category = category;
        this.content = content;
//        this.thumbnailUrl = thumbnailUrl;
    }

    // ✅ 요청 DTO -> 엔티티 변환
    public static StudyChannel from(User owner, CreateStudyChannelRequest req) {
        return StudyChannel.builder()
                .owner(owner)
//                .category(req.getCategory())
                .content(req.getContent())
                .build();
    }

    // ✅ 수정 메서드
    public void update(UpdateStudyChannelRequest req) {
        if (req.getContent() != null) this.content = req.getContent();
//        if (req.getCategory() != null) this.category = req.getCategory();
    }
}



