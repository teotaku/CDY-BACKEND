// src/main/java/com/cdy/cdy/entity/StudyChannel.java
package com.cdy.cdy.entity;

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

    @Enumerated(EnumType.STRING) @Column(length = 30)
    private StudyCategory category; // 카테고리

    @Column(length = 200, nullable = false)
    private String title; // 제목

    @Lob
    // ↑ Large Object.
    //   String + @Lob => CLOB/TEXT(매우 긴 본문 저장)
    //   byte[] + @Lob => BLOB(바이너리)
    private String content; // 본문

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl; // 썸네일 이미지 URL(S3 등 외부 저장)

    public static StudyChannel create(User owner, StudyCategory category, String title, String content, String thumbnailUrl) {
        Objects.requireNonNull(owner); Objects.requireNonNull(title);
        var s = new StudyChannel();
        s.owner = owner; s.category = category; s.title = title; s.content = content; s.thumbnailUrl = thumbnailUrl;
        return s;
    }

    public void update(String title, String content, StudyCategory category, String thumbnailUrl) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (category != null) this.category = category;
        this.thumbnailUrl = thumbnailUrl; // null이면 삭제 의도 허용
    }
}



