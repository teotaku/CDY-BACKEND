// src/main/java/com/cdy/cdy/entity/ProjectAnswer.java
package com.cdy.cdy.entity.project;

import com.cdy.cdy.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "project_answers",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_answer_question_member",
                columnNames = {"question_id", "project_member_id"}  // 같은 신청서의 같은 질문에 답은 1개만
        )
)
public class ProjectAnswer extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ↑ 답변:N - 질문:1
    @JoinColumn(name = "question_id", nullable = false)
    private ProjectQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)            // N:1  ←★ 여기!
    @JoinColumn(name = "project_member_id", nullable = false)
    private ProjectMember member;

    @Column(name = "answer_text")
    private String answerText; // 긴 텍스트


}
