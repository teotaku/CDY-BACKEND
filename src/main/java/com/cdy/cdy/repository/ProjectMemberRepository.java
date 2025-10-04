package com.cdy.cdy.repository;

import com.cdy.cdy.entity.project.Project;
import com.cdy.cdy.entity.project.ProjectMember;
import com.cdy.cdy.entity.project.ProjectMemberStatus;
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
          and pm.status = com.cdy.cdy.entity.project.ProjectMemberStatus.APPLIED
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



    @Query(value = """
        SELECT pm.*
        FROM project_member pm
        JOIN `user` u ON pm.user_id = u.id
        WHERE pm.project_id = :projectId
        AND pm.status IN ('APPROVED', 'COMPLICATED')
        ORDER BY pm.joined_at DESC
        """, nativeQuery = true)
    List<ProjectMember> findApprovedAndComplicatedMembersWithUserByProjectId(@Param("projectId") Long projectId);



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
            and pm.status = 'APPROVED'
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
    @Query(value = """
    SELECT COUNT(*) 
    FROM project_member pm
    WHERE pm.project_id = :projectId
      AND pm.status = :status
    """, nativeQuery = true)
    long countByProject_IdAndStatus(
            @Param("projectId") Long projectId,
            @Param("status") String status
    );

    // 1) 특정 프로젝트에 기존 신청/참여 기록 찾기
    @Query(value = """
    SELECT * 
    FROM project_member pm
    WHERE pm.user_id = :userId
      AND pm.project_id = :projectId
    LIMIT 1
    """, nativeQuery = true)
    Optional<ProjectMember> findByUser_IdAndProject_Id(
            @Param("userId") Long userId,
            @Param("projectId") Long projectId
    );

    //다른 프로젝트에 신청,승인상태인지 확인
    @Query("""
    select case when count(pm) > 0 then true else false end
    from ProjectMember pm
    where pm.user.id = :userId
      and pm.status in :statuses
""")
    boolean existsByUser_IdAndStatusIn(
            @Param("userId") Long userId,
            @Param("statuses") List<String> statuses
    );


    @Query("""
       select pm
       from ProjectMember pm
       join fetch pm.user u
       where pm.project.id = :projectId
         and pm.status = 'APPLIED'
       order by pm.joinedAt asc
       """)
    List<ProjectMember> findAllApplicantsWithUser(@Param("projectId") Long projectId);


    @Query("""
    select p
    from ProjectMember pm
    join pm.project p
    where pm.user.id = :userId
      and pm.status = 'COMPLICATED'
      and p.status = 'COMPLETED'
    """)
    List<Project> findUserCompletedProjects(@Param("userId") Long userId);

}

