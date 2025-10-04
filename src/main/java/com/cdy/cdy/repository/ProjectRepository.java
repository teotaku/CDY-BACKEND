package com.cdy.cdy.repository;

import com.cdy.cdy.entity.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {


    //완료된 프로젝트 제외하고 프로젝트 전체조회
    @Query("SELECT p FROM Project p WHERE p.status <> 'COMPLETED'")
    Page<Project> findAllExcludeCompleted(Pageable pageable);


    @Query("SELECT pm.project " +
            "FROM ProjectMember pm " +
            "WHERE pm.user.id = :userId " +
            "AND pm.status = ProjectMemberStatus.APPROVED")
    Optional<Project> findApprovedProjectsByUserId(@Param("userId") Long userId);


    @Query("""
            SELECT p FROM Project p
            JOIN p.projectMembers m
            WHERE m.user.id = :userId
            AND m.status = com.cdy.cdy.entity.project.ProjectMemberStatus.APPROVED
            AND p.status = com.cdy.cdy.entity.project.ProjectStatus.IN_PROGRESS
            """)
    Optional<Project> findProgressingProjectByUserId(@Param("userId") Long userId);


    @Query("""
        select distinct p
        from Project p
        join fetch p.manager m
        left join fetch p.projectMembers pm
        left join fetch pm.user u
        where p.id = :projectId
        """)
    Optional<Project> findWithManagerAndMembers(@Param("projectId") Long projectId);

    // 프로젝트의 팀장(userId)만 뽑아오기 (가볍게)
    @Query("select p.manager.id from Project p where p.id = :projectId")
    Optional<Long> findManagerId(@Param("projectId") Long projectId);
}

