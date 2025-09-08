package com.cdy.cdy.repository;

import com.cdy.cdy.entity.ProjectMemberStatus;
import com.cdy.cdy.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {


    @Query("SELECT pm.project " +
            "FROM ProjectMember pm " +
            "WHERE pm.user.id = :userId " +
            "AND pm.status = ProjectMemberStatus.APPROVED")
    Optional<Project> findApprovedProjectsByUserId(@Param("userId") Long userId);
}
