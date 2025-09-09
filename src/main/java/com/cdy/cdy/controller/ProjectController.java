package com.cdy.cdy.controller;

import com.cdy.cdy.dto.request.CreateProjectQuestionRequest;
import com.cdy.cdy.dto.request.CreateProjectRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.ProjectResponse;
import com.cdy.cdy.entity.ProjectMemberRole;
import com.cdy.cdy.service.ProjectService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<String> createProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateProjectRequest request
    ) {
        Long userId = userDetails.getId();
         projectService.createProject(userId, request);
        return ResponseEntity.ok("프로젝트가 생성되었습니다");
    }

    //프로젝트 전체조회
    @GetMapping("/findAll")
    public ResponseEntity<Page<ProjectResponse>> getAllProjectes
            (@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
             Pageable pageable
             ) {

        return ResponseEntity.ok(projectService.findAll(pageable));

    }

    //단일 프로젝트 조회
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> findOneById(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.findOneById(projectId));
    }

    //신청중인 프로젝트 조회
    @GetMapping("/find/applied")
    public ResponseEntity<ProjectResponse> getApplyProject
    (@AuthenticationPrincipal CustomUserDetails userDetails) {

        ProjectResponse projectResponse = projectService.getApplyProject(userDetails.getId());
        return ResponseEntity.ok(projectResponse);
    }

    //진행중인 프로젝트 조회
    @GetMapping("/find/progressing")
    public ResponseEntity<ProjectResponse> getProgressingProject(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProjectResponse projectResponse = projectService.getProgressingProject(userDetails.getId());
        return ResponseEntity.ok(projectResponse);
    }

    //프로젝트 신청하기
    @PostMapping("/apply/{projectId}")
    public ResponseEntity<String> applyProject(@PathVariable Long projectId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestBody CreateProjectQuestionRequest ProjectQuestion
    ) {
        projectService.applyToProject(userDetails.getId(), projectId, ProjectQuestion);
        return ResponseEntity.ok("프로젝트 신청 완료");
    }

}
