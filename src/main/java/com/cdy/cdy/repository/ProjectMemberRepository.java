package com.cdy.cdy.repository;

import com.cdy.cdy.entity.proejct.ProjectMember;
import com.cdy.cdy.entity.proejct.ProjectMemberStatus;
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
        select pm
        from ProjectMember pm
        join fetch pm.user u
        where pm.project.id = :projectId
          and pm.status = com.cdy.cdy.entity.ProjectMemberStatus.APPLIED
        """)
    List<ProjectMember> findApplicants(@Param("projectId") Long projectId);


    @Query("""
        select pm
        from ProjectMember pm
        join fetch pm.user u
        where pm.project.id = :projectId
          and pm.user.id = :userId
        """)
    Optional<ProjectMember> findByProjectIdAndUserId(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId
    );




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
    // ✅ findFirstByUser_IdOrderByJoinedAtDesc
    /*
    @Query("""
        select pm
        from ProjectMember pm
        where pm.user.id = :userId
        order by pm.joinedAt desc
    """)
    Optional<ProjectMember> findLatestByUserId(@Param("userId") Long userId);
    */
    Optional<ProjectMember> findFirstByUser_IdOrderByJoinedAtDesc(Long userId);


    // 진행중(승인된) or 신청한 프로젝트 1건
    Optional<ProjectMember> findTopByUserIdAndStatusOrderByJoinedAtDesc(
            Long userId, ProjectMemberStatus status);


    // 멤버 수 카운트 (앞서 쓰던 것)
    @Query("""
            select count(pm)
             from ProjectMember pm
            where pm.project.id = :projectId
            and pm.status = com.cdy.cdy.entity.ProjectMemberStatus.APPROVED
            """)
    long countByApprovedPm(@Param("projectId") Long projectId);


    // 유저가 해당 프로젝트에 이미 신청/참여 중인지(중복 신청 방지)
    // ✅ existsByUser_IdAndProject_IdAndStatusIn
    /*
    @Query("""
        select case when count(pm) > 0 then true else false end
        from ProjectMember pm
        where pm.user.id = :userId
          and pm.project.id = :projectId
          and pm.status in :statuses
    """)
    boolean existsUserInProjectWithStatuses(
        @Param("userId") Long userId,
        @Param("projectId") Long projectId,
        @Param("statuses") Collection<ProjectMemberStatus> statuses
    );
    */
    boolean existsByUser_IdAndProject_IdAndStatusIn(Long userId, Long projectId,
                                                    Collection<ProjectMemberStatus> statuses);

    // 현재 승인된(진행중) 인원 수 (정원 체크용)

    // ✅ countByProject_IdAndStatus
    /*
    @Query("""
        select count(pm)
        from ProjectMember pm
        where pm.project.id = :projectId
          and pm.status = :status
    """)
    long countByProjectAndStatus(
        @Param("projectId") Long projectId,
        @Param("status") ProjectMemberStatus status
    );
    */
    long countByProject_IdAndStatus(Long projectId, ProjectMemberStatus status);

    // 승인/신청 레코드 직접 찾고 싶을 때
    Optional<ProjectMember> findByUser_IdAndProject_Id(Long userId, Long projectId);


    // ✅ existsByUser_IdAndStatusIn
    /*
    @Query("""
        select case when count(pm) > 0 then true else false end
        from ProjectMember pm
        where pm.user.id = :userId
          and pm.status in :statuses
    """)
    boolean existsUserWithStatuses(
        @Param("userId") Long userId,
        @Param("statuses") Collection<ProjectMemberStatus> statuses
    );
    */
    boolean existsByUser_IdAndStatusIn(Long userId, List<ProjectMemberStatus> applied);


    @Query("""
       select pm
       from ProjectMember pm
       join fetch pm.user u
       where pm.project.id = :projectId
         and pm.status = 'APPLIED'
       order by pm.joinedAt asc
       """)
    List<ProjectMember> findAllApplicantsWithUser(@Param("projectId") Long projectId);

}

