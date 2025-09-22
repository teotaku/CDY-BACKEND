// src/main/java/com/cdy/cdy/repository/ProjectApplicationRepository.java
package com.cdy.cdy.repository;

import com.cdy.cdy.entity.project.ProjectApplication;
import com.cdy.cdy.entity.ApplicationsStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {

    // 같은 프로젝트에 같은 지원자가 이미 신청/승인 등인지 체크
    boolean existsByProject_IdAndApplicant_IdAndStatusIn(
            Long projectId, Long applicantId, Collection<ApplicationsStatus> statuses
    );
}
