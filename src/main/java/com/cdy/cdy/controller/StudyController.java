package com.cdy.cdy.controller;


import com.cdy.cdy.dto.request.CreateStudyChannelRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.StudyChannelResponse;
import com.cdy.cdy.entity.StudyChannel;
import com.cdy.cdy.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @PostMapping("/create")
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
    @GetMapping("/getAll")
    public ResponseEntity<Page<StudyChannelResponse>> getAllStudies
    (@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
     Pageable pageable) {

        return ResponseEntity.ok(studyService.getAllStudies(pageable));
    }

    //카테고리별 조회
    @GetMapping("/{category}")
    public ResponseEntity<Page<StudyChannelResponse>> findByCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String Category,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(studyService.findByCategory(userDetails.getId(),Category,pageable));
    }


    @DeleteMapping("/delete/{studyId}")
    public ResponseEntity<Void> deleteStudy(@PathVariable Long studyId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        studyService.deleteStudy(studyId, userDetails.getId());
        return ResponseEntity.ok().build();

    }
}





