package com.cdy.cdy.controller;


import com.cdy.cdy.dto.request.CreateStudyChannelRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.StudyChannelResponse;
import com.cdy.cdy.entity.StudyChannel;
import com.cdy.cdy.service.AuthService;
import com.cdy.cdy.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    //스터디 생성
    @Operation(summary = "스터디 생성", description = "로그인한 사용자가 새로운 스터디를 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<StudyChannelResponse> createStudy(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CreateStudyChannelRequest request) {
        return ResponseEntity.ok(studyService.createStudy(user.getId(), request));
    }

    // 단건 조회
    @Operation(summary = "스터디 단건 조회", description = "스터디 ID를 기반으로 특정 스터디 정보를 조회합니다.")
    @GetMapping("/{studyId}")
    public ResponseEntity<StudyChannelResponse> getStudy(@PathVariable Long studyId
            , @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(studyService.getStudy(studyId));
    }

    // 전체 조회
    @GetMapping("/getAllStudies")
    @Operation(summary = "스터디 전체 조회", description = "전체 스터디 목록을 조회합니다.")
    public ResponseEntity<List<StudyChannelResponse>> getAllStudies() {
        return ResponseEntity.ok(studyService.getAllStudies());
    }
    @Operation(summary = "스터디 삭제", description = "로그인한 사용자가 특정 스터디를 삭제합니다.")
    @DeleteMapping("/delete/{studyId}")
    public ResponseEntity<Void> deleteStudy(@PathVariable Long studyId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        studyService.deleteStudy(studyId, userDetails.getId());
        return ResponseEntity.ok().build();

    }
}





