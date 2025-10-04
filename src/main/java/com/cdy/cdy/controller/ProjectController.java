package com.cdy.cdy.controller;

import com.cdy.cdy.dto.request.ApplyProjectRequest;
import com.cdy.cdy.dto.request.CreateProjectRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;

import com.cdy.cdy.dto.response.project.AllProjectResponse;
import com.cdy.cdy.dto.response.project.ApplyingProjectResponse;
import com.cdy.cdy.dto.response.project.OneProjectResponse;
import com.cdy.cdy.dto.response.project.ProjectApplicationResponse;
import com.cdy.cdy.dto.response.project.ProgressingProjectResponse;
import com.cdy.cdy.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/project")
public class ProjectController {


    private final ProjectService projectService;


    //메인페이지 프로젝트 반환



    //프로젝트 생성
    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
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
    @Operation(summary = "프로젝트 전체 조회", description = "모든 프로젝트를 페이징 처리하여 최신순으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = AllProjectResponse.class)))
    @GetMapping("/findAll")
    public ResponseEntity<Page<AllProjectResponse>> getAllProjectes
    (       @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
     Pageable pageable
    ) {
        return ResponseEntity.ok(projectService.findAllExcludeCompleted(pageable));

    }

    //단일 프로젝트 조회
    @Operation(summary = "단일 프로젝트 조회", description = "프로젝트 ID로 단일 프로젝트 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = OneProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트 없음")
    })
    @GetMapping("/{projectId}")
    public ResponseEntity<OneProjectResponse> findOneById(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.findOneById(projectId));
    }

    //신청중인 프로젝트 조회
    @Operation(summary = "신청중인 프로젝트 조회", description = "현재 로그인한 사용자가 신청중인 프로젝트 정보를 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ApplyingProjectResponse.class)))
    @GetMapping("/find/applied")
    public ResponseEntity<ApplyingProjectResponse> getApplyProject
    (@AuthenticationPrincipal CustomUserDetails userDetails) {

        ApplyingProjectResponse applyProject = projectService.getApplyProject(userDetails.getId());
        return ResponseEntity.ok(applyProject);
    }

    //진행중인 프로젝트 조회
    @Operation(summary = "진행중인 프로젝트 조회", description = "현재 로그인한 사용자가 참여 중인 프로젝트 정보를 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ProgressingProjectResponse.class)))
    @GetMapping("/find/progressing")
    public ResponseEntity<ProgressingProjectResponse> getProgressingProject(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProgressingProjectResponse progressingProject = projectService.getProgressingProject(userDetails.getId());
        return ResponseEntity.ok(progressingProject);
    }

    //프로젝트 신청하기
    @Operation(summary = "프로젝트 신청", description = "특정 프로젝트에 신청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신청 성공",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/apply/{projectId}")
    public ResponseEntity<String> applyProject(@PathVariable Long projectId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestBody ApplyProjectRequest ApplyProjectRequest
    ) {

        projectService.applyToProject(userDetails.getId(), projectId, ApplyProjectRequest);
        return ResponseEntity.ok("프로젝트 신청 완료");
    }

    //신청자 목록 가져오기

    @Operation(summary = "프로젝트 신청자 목록 조회", description = "해당 프로젝트의 신청자 목록을 조회합니다. (팀장 권한 필요)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProjectApplicationResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/projectApplication/{projectId}/applicants")
    public ResponseEntity<List<ProjectApplicationResponse>> getApplication(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        List<ProjectApplicationResponse> application =
                projectService.getApplication(userDetails.getId(), projectId);
        return ResponseEntity.ok(application);
    }
}
