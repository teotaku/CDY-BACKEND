// src/main/java/com/cdy/cdy/entity/ProjectQuestion.java
package com.cdy.cdy.entity.project;

import com.cdy.cdy.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "project_questions")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectQuestion extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 질문:N - 프로젝트:1
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "question_text", length = 500, nullable = false)
    private String questionText; // 질문 내용

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder ; // 노출 순서

    public static ProjectQuestion create(Project project, String text, int order) {
        Objects.requireNonNull(project); Objects.requireNonNull(text);
        var q = new ProjectQuestion();
        q.project = project; q.questionText = text; q.displayOrder = (order <= 0 ? 1 : order);
        return q;
    }



    public void changeOrder(int newOrder) { this.displayOrder = Math.max(1, newOrder); }
    public void changeText(String text) { this.questionText = Objects.requireNonNull(text); }
}
