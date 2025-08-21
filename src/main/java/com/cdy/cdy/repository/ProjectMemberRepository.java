package com.cdy.cdy.repository;

import com.cdy.cdy.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember,Long> {
    List<ProjectMember> findByUserId(Long userId);

    Optional<ProjectMember> findFirstByUser_IdOrderByJoinedAtDesc(Long userId);
}

