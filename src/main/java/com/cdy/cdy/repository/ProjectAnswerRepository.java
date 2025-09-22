// src/main/java/com/cdy/cdy/repository/ProjectAnswerRepository.java
package com.cdy.cdy.repository;

import com.cdy.cdy.entity.project.ProjectAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectAnswerRepository extends JpaRepository<ProjectAnswer, Long> {

    List<ProjectAnswer> findByMember_Id(Long memberId);
    Optional<ProjectAnswer> findByMember_IdAndQuestion_Id(Long memberId, Long questionId);



    // 방금 받아온 신청자들(memberIds)의 답변을 질문 순서대로 한 번에 로딩
    @Query("""
        select pa
        from ProjectAnswer pa
        join fetch pa.question q
        where pa.member.id in :memberIds
        order by q.displayOrder asc
        """)
    List<ProjectAnswer> findAllByMemberIdInOrderByDisplayOrder(
            @Param("memberIds") List<Long> memberIds);



    @Query("""
       select pa
       from ProjectAnswer pa
       join fetch pa.question q
       join pa.member m
       where m.id in :memberIds
       order by m.id asc, q.displayOrder asc
       """)
    List<ProjectAnswer> findAllByMemberIdInOrderByMemberAndQuestion(@Param("memberIds") List<Long> memberIds);


}
