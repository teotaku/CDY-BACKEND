package com.cdy.cdy.controller;

import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.ProjectCompleteResponse;
import com.cdy.cdy.dto.response.project.ProjectQuestionResponse;
import com.cdy.cdy.service.ProjectApplicantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projectApplication")
public class ProjectApplicantController {

    private final ProjectApplicantService projectApplicantService;


    // 신청자 승인
    @Operation(
            summary = "프로젝트 신청자 승인",
            description = "팀장이 특정 프로젝트의 신청자를 승인합니다. 팀장 권한이 없는 경우 403 오류가 발생합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "403", description = "팀장 권한 없음"),
            @ApiResponse(responseCode = "404", description = "프로젝트 또는 신청 내역 없음")
    })
    @PostMapping("{projectId}/applicants/{userId}/approve")
    public ResponseEntity<String> approve(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        projectApplicantService.approve(projectId, userId, userDetails.getId());
        return ResponseEntity.ok("승인이 완료되었습니다.");
    }

    // 신청자 거절
    @Operation(
            summary = "프로젝트 신청자 거절",
            description = "팀장이 특정 프로젝트의 신청자를 거절합니다. 팀장 권한이 없는 경우 403 오류가 발생합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "거절 성공"),
            @ApiResponse(responseCode = "403", description = "팀장 권한 없음"),
            @ApiResponse(responseCode = "404", description = "프로젝트 또는 신청 내역 없음")
    })
    @PostMapping("{projectId}/applicants/{userId}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        projectApplicantService.reject(projectId, userId, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    //프로젝트 질문가져오기
    @Operation(
            summary = "프로젝트 질문 조회",
            description = "특정 프로젝트에 등록된 질문 리스트를 조회합니다. (지원 시 표시할 질문)"
    )
    @GetMapping("/questions/{projectId}")
    public ResponseEntity<List<ProjectQuestionResponse>> getQuestions(@PathVariable Long projectId) {
        List<ProjectQuestionResponse> questions = projectApplicantService.getQuestions(projectId);
        return ResponseEntity.ok(questions);
    }

    @Operation
            (summary = "프로젝트 취소하기",
                    description = "프로젝트 id 넘기고 로그인된 유저가 해당 프로젝트 신청중이면 취소")
    //프로젝트 취소하기
    @PostMapping("/cancel/{projectId}")
    public ResponseEntity<String> cancel(@PathVariable Long projectId,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        projectApplicantService.cancel(projectId, userDetails.getId());
        return ResponseEntity.ok("프로젝트 신청이 취소되었습니다.");

    }
    //프로젝트 완료하기
    @Operation
            (summary = "프로젝트 완료하기",
                    description = "프로젝트 id 넘기고 로그인된 유저가 해당 프로젝트 진행중이면 완료")
    @PostMapping("/complete/{projectId}")
    public ResponseEntity<ProjectCompleteResponse> complete(@PathVariable Long projectId,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProjectCompleteResponse complete = projectApplicantService.complete(projectId, userDetails.getId());
        return ResponseEntity.ok(complete);

    }
}
