package com.cdy.cdy.controller.admin;

import com.cdy.cdy.dto.admin.AdminHomeResponseDto;
import com.cdy.cdy.dto.admin.CursorResponse;
import com.cdy.cdy.dto.request.SignUpRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.project.AdminProjectResponse;
import com.cdy.cdy.dto.response.study.AdminStudyResponse;
import com.cdy.cdy.service.AuthService;
import com.cdy.cdy.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;


    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "회원가입")
    @PostMapping("/create")
    public ResponseEntity<String> createAdmin(@RequestBody SignUpRequest signUpRequest,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {

        adminService.createdAdmin(signUpRequest);
        return ResponseEntity.ok("ADMIN회원가입 완료");
    }


    @Operation(summary = "회원가입 admin용")
    @PostMapping("/createAdmin")
    public ResponseEntity<String> createUser(@RequestBody SignUpRequest signUpRequest,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        adminService.createdAdmin(signUpRequest);
        return ResponseEntity.ok("회원가입 완료");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "홈화면 데이터")
    @GetMapping("/home")
    public ResponseEntity<CursorResponse<AdminHomeResponseDto>> homeData(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                         @RequestParam(required = false) Long lastUserId,
                                                                         @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(adminService.getHomeData(lastUserId, limit));

    }


    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "오프라인 참가 횟수 변경", description = "유저 아이디와(userId) " +
            ", 오프라인참여횟수(count)를 받고 오프라인 참가 횟수 수정 ")
    @PostMapping("/updateOffline")
    public ResponseEntity<String> updateOffline(@RequestParam Long count,
                                                @RequestParam Long userId) {
        adminService.updateOffline(count, userId);

        return ResponseEntity.ok("값이 변경되었습니다 (" + count + ")");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "스터디 조회", description = "관리자가 전체 유저의 스터디목록조회(페이징처리)")
    @GetMapping("/findStudyList")
    public ResponseEntity<Page<AdminStudyResponse>> findStudyList(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {


        return ResponseEntity.ok(adminService.findStudyList(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "프로젝트 조회", description = "관리자가 전체 유저의 프로젝트목록조회(페이징처리)")
    @GetMapping("/findProjectList")
    public ResponseEntity<Page<AdminProjectResponse>> findProjectList(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(adminService.findProjectList(pageable));

    }
}