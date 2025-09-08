package com.cdy.cdy.repository;

import com.cdy.cdy.entity.ProjectMember;
import com.cdy.cdy.entity.ProjectMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember,Long> {
    List<ProjectMember> findByUserId(Long userId);



    @Query("""
       SELECT pm 
       FROM ProjectMember pm
       JOIN FETCH pm.user u
       WHERE pm.project.id = :projectId
         AND pm.status = ProjectMemberStatus.APPROVED
       ORDER BY pm.joinedAt DESC
    """)
    List<ProjectMember> findApprovedMembersWithUserByProjectId(@Param("projectId") Long projectId);



    //최근 참가 이력 1건
    Optional<ProjectMember> findFirstByUser_IdOrderByJoinedAtDesc(Long userId);


    // 진행중(승인된) or 신청한 프로젝트 1건
    Optional<ProjectMember> findTopByUserIdAndStatusOrderByJoinedAtDesc(
            Long userId, ProjectMemberStatus status);


    // 멤버 수 카운트 (앞서 쓰던 것)
    long countByProjectId(Long projectId);


    // 유저가 해당 프로젝트에 이미 신청/참여 중인지(중복 신청 방지)
    boolean existsByUser_IdAndProject_IdAndStatusIn(Long userId, Long projectId,
                                                    Collection<ProjectMemberStatus> statuses);

    // 현재 승인된(진행중) 인원 수 (정원 체크용)
    long countByProject_IdAndStatus(Long projectId, ProjectMemberStatus status);

    // 승인/신청 레코드 직접 찾고 싶을 때
    Optional<ProjectMember> findByUser_IdAndProject_Id(Long userId, Long projectId);

    boolean existsByUser_IdAndStatusIn(Long userId, List<ProjectMemberStatus> applied);

}

