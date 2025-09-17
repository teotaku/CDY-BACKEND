package com.cdy.cdy.controller;

import com.cdy.cdy.dto.response.CustomUserDetails;
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
    public ResponseEntity<Void> approve(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        projectApplicantService.approve(projectId, userId,userDetails.getId());
        return ResponseEntity.ok().build();
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
        projectApplicantService.reject(projectId, userId,userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
