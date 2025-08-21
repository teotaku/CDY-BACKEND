package com.cdy.cdy.controller;

import com.cdy.cdy.dto.request.CreateProjectRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.ProjectAnswerResponse;
import com.cdy.cdy.dto.response.ProjectResponse;
import com.cdy.cdy.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/project")
public class ProjectController {


    private final ProjectService projectService;


    @PostMapping("/create")
    public ResponseEntity<ProjectResponse> createProject(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateProjectRequest request
    ) {
        Long userId = principal.getId();
        ProjectResponse res = projectService.createProject(userId, request);
        return ResponseEntity.ok(res);
    }
}
