// src/main/java/com/cdy/cdy/entity/TechTag.java
package com.cdy.cdy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tech_tags")
public class TechTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    // ↑ 같은 태그명 중복 생성 방지
    private String name;

    public static TechTag create(String name) {
        var t = new TechTag();
        t.name = Objects.requireNonNull(name);
        return t;
    }

    public void rename(String newName) { this.name = Objects.requireNonNull(newName); }
}