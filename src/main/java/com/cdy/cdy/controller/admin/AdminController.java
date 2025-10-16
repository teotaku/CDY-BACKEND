package com.cdy.cdy.controller.admin;

import com.cdy.cdy.dto.admin.AdminHomeResponseDto;
import com.cdy.cdy.dto.admin.CursorResponse;
import com.cdy.cdy.dto.request.SignUpRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
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
    @Operation(summary = "ADMIN권한 회원가입 테스트용")
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





}
