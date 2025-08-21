package com.cdy.cdy.controller;


import com.cdy.cdy.dto.request.CreateStudyChannelRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.StudyChannelResponse;
import com.cdy.cdy.entity.StudyChannel;
import com.cdy.cdy.service.StudyService;
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

    @PostMapping
    public ResponseEntity<StudyChannelResponse> createStudy(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CreateStudyChannelRequest request) {
        return ResponseEntity.ok(studyService.createStudy(user.getId(), request));
    }

    // 단건 조회
    @GetMapping("/{studyId}")
    public ResponseEntity<StudyChannelResponse> getStudy(@PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.getStudy(studyId));
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<StudyChannelResponse>> getAllStudies() {
        return ResponseEntity.ok(studyService.getAllStudies());
    }
}





