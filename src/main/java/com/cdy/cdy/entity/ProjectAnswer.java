// src/main/java/com/cdy/cdy/entity/ProjectAnswer.java
package com.cdy.cdy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "project_answers",
        uniqueConstraints = @UniqueConstraint( // ← 복합 유니크
                name = "uk_answer_question_application",
                columnNames = {"question_id", "application_id"} // 같은 신청서의 같은 질문에 답은 1개만
        )
)
public class ProjectAnswer extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 답변:N - 질문:1
    @JoinColumn(name = "question_id", nullable = false)
    private ProjectQuestion question;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 답변:N - 신청:1
    @JoinColumn(name = "application_id", nullable = false)
    private ProjectApplication application;

    @Lob @Column(name = "answer_text")
    private String answerText; // 긴 텍스트

    public static ProjectAnswer write(ProjectQuestion question, ProjectApplication app, String text) {
        Objects.requireNonNull(question); Objects.requireNonNull(app);
        var a = new ProjectAnswer();
        a.question = question; a.application = app; a.answerText = text;
        return a;
    }

    public void modify(String newText) { this.answerText = newText; }
}
