package com.cdy.cdy.controller;

import com.cdy.cdy.dto.response.ApplicantCardResponse;
import com.cdy.cdy.service.ProjectApplicantService;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projectApplication")
public class ProjectApplicantController {

    private final ProjectApplicantService projectApplicantService;

    //신청자 확인
    @GetMapping("{projectId}/applicants")
    public ResponseEntity<List<ApplicantCardResponse>> getApplicants(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(projectApplicantService.getApplicants(projectId));
    }

    // 신청자 승인
    @PostMapping("{projectId}/applicants/{userId}/approve")
    public ResponseEntity<Void> approve(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        projectApplicantService.approve(projectId, userId);
        return ResponseEntity.ok().build();
    }

    // 신청자 거절
    @PostMapping("{projectId}/applicants/{userId}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        projectApplicantService.reject(projectId, userId);
        return ResponseEntity.ok().build();
    }
}
