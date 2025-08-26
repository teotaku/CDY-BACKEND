package com.cdy.cdy.controller;

import com.cdy.cdy.dto.request.CreateProjectRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.ProjectResponse;
import com.cdy.cdy.entity.ProjectMemberRole;
import com.cdy.cdy.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/project")
public class ProjectController {


    private final ProjectService projectService;

    //프로젝트 생성
    @PostMapping("/create")
    public ResponseEntity<ProjectResponse> createProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateProjectRequest request
    ) {
        Long userId = userDetails.getId();
        ProjectResponse res = projectService.createProject(userId, request);
        return ResponseEntity.ok(res);
    }

    //신청중인 프로젝트 조회
    public ResponseEntity<ProjectResponse> getApplyProject
    (@AuthenticationPrincipal CustomUserDetails userDetails) {
       ProjectResponse projectResponse = projectService.getApplyProject(userDetails.getId());
        return ResponseEntity.ok(projectResponse);
    }

    //진행중인 프로젝트 조회
    public ResponseEntity<ProjectResponse> getProgressingProject(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProjectResponse projectResponse = projectService.getProgressingProject(userDetails.getId());
        return ResponseEntity.ok(projectResponse);
    }

    @PostMapping("/projects/apply{projectId}")
    public ResponseEntity<Void> applyProject(@PathVariable Long projectId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestParam(required = false) ProjectMemberRole role) {
        projectService.applyToProject(userDetails.getId(), projectId, role);
        return ResponseEntity.ok().build(); // 또는 201/204
    }
}
