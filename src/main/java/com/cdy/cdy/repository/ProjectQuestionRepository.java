package com.cdy.cdy.repository;

import com.cdy.cdy.entity.ProjectQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// ProjectQuestionRepository.java
@Repository
public interface ProjectQuestionRepository extends JpaRepository<ProjectQuestion, Long> {

    // 질문을 고정된 순서로 전부 가져오기 (order 컬럼이 있으면 그걸로, 없으면 id 기준)

    @Query("""
    select q
    from ProjectQuestion q
    where q.project.id = :projectId
    order by q.displayOrder asc
""")
    List<ProjectQuestion> findAllByProjectIdOrderByDisplayOrder(Long projectId);
    // 만약 order 같은 정렬 필드가 있으면:
    // List<ProjectQuestion> findAllByProject_IdOrderByOrderAsc(Long projectId);
}
