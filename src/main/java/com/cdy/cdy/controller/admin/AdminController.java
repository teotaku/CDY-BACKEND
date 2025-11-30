package com.cdy.cdy.controller.admin;

import com.cdy.cdy.dto.admin.*;
import com.cdy.cdy.dto.request.LoginRequest;
import com.cdy.cdy.dto.request.SignUpRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.LoginResponse;
import com.cdy.cdy.dto.response.project.SingleProjectResponse;
import com.cdy.cdy.dto.response.study.StudyChannelResponse;
import com.cdy.cdy.dto.response.project.AdminProjectResponse;
import com.cdy.cdy.dto.response.study.AdminStudyResponse;
import com.cdy.cdy.service.AuthService;
import com.cdy.cdy.service.StudyService;
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

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;
    private final StudyService studyService;


    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "회원가입", description = "관리자(ADMIN권한)가 일반유저 생성,회원가입 ")
    @PostMapping("/create")
    public ResponseEntity<String> createAdmin(@RequestBody SignUpRequest signUpRequest,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {

        authService.join(signUpRequest);
        return ResponseEntity.ok("일반계정 회원가입 완료");
    }


    @Operation(summary = "회원가입 admin용", description = "어드민 계정 생성용")
    @PostMapping("/createAdmin")
    public ResponseEntity<String> createUser(@RequestBody SignUpRequest signUpRequest,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        adminService.createdAdmin(signUpRequest);
        return ResponseEntity.ok("어드민계정 생성 완료");
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
    @Operation(summary = "유저 정보", description = "유저이름,연락처,이메일,비밀번호,포지션,가입일 조회,커서스크롤 형태")
    @GetMapping("/getUserInfoList")
    public ResponseEntity<?> UserInfoList(@RequestParam(required = false) Long lastUserId,
                                          @RequestParam(defaultValue = "10") int limit) {
        CursorResponse<UserInfoResponse> result = adminService.getUserInfoList(lastUserId, limit);
        return ResponseEntity.ok(result);

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


    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "프로젝트 상세 조회", description = "프로젝트 id, 설명, 로고이미지 반환")
    @GetMapping("/findSingleProject/{id}")
    public ResponseEntity<SingleProjectResponse> findSingleProject(@PathVariable Long id) {

        SingleProjectResponse dto = adminService.getSingleProject(id);
        return ResponseEntity.ok(dto);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "스터디 삭제", description = "관리자가 스터디 id를 파라미터로 받고 스터디를 삭제")
    @DeleteMapping("/deleteStudy")
    public ResponseEntity<?> deleteStudy(@RequestBody DeleteStudyReason dto) {

        adminService.deleteStudy(dto);
        return ResponseEntity.ok("스터디가 삭제되었습니다.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "스터디 상세 조회", description = "관리자가 스터디 id를 파라미터로 받고 스터디를 조회")
    @GetMapping("/findStudy/{studyId}")
    public ResponseEntity<StudyChannelResponse> findStudyById(@PathVariable Long studyId) {

        StudyChannelResponse result = studyService.getStudy(studyId);

        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "배너추가", description = "프론트에서 presign을 통해 얻은 imagekey를 넘겨주면 관리자가 배너를 추가")
    @PostMapping("/addBanner")
    public ResponseEntity<?> addBanner(@RequestBody CreateBanner createBanner) {

        adminService.addBanner(createBanner);
        return ResponseEntity.ok("배너가 추가되었습니다.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "배너 전체 조회", description = "배너id,이미지url 반환")
    @GetMapping("/findAllBanner")
    public ResponseEntity<List<BannerResponseDto>> findAllBanners() {
        List<BannerResponseDto> dto = adminService.findAllBanner();
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "배너 단건 조회", description = "배너 id를 받고 해당 배너id,이미지url 반환")
    @GetMapping("/findOneBanner/{bannerId}")
    public ResponseEntity<BannerResponseDto> findOneBanner(@PathVariable Long bannerId) {
        BannerResponseDto dto = adminService.findOneBanner(bannerId);

        return ResponseEntity.ok(dto);
    }

    //관리자 로그인
    @PostMapping("/login")
    @Operation(summary = "관리자 로그인")
    public ResponseEntity<LoginResponse> adminLogin(@RequestBody LoginRequest loginRequest) {

        LoginResponse token = adminService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "파트너 추가", description = "파트너이름, 이미지key 받고 파트저 저장")
    @PostMapping("/addPartner")
    public ResponseEntity<?> addPartner(@RequestBody CreatePartner createPartner) {
        adminService.AddPartner(createPartner);
        return ResponseEntity.ok("파트너가 저장되었습니다.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "파트너 전체 조회", description = "관리자가 파트너 전체 조회")
    @GetMapping("/findAllPartner")
    public ResponseEntity<List<PartnerResponseDto>> findAllPartners() {
        List<PartnerResponseDto> result = adminService.findAllPartners();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "파트너 단건 조회", description = "파트너 단건 조회 id,imageurl 반환")
    @GetMapping("/findOnePartner/{id}")
    public ResponseEntity<PartnerResponseDto> findOnePartner(Long id) {
        PartnerResponseDto result = adminService.findOnePartner(id);
        return ResponseEntity.ok(result);
    }
}