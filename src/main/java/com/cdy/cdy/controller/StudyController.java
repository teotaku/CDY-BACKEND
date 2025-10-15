package com.cdy.cdy.controller;


import com.cdy.cdy.dto.request.CreateStudyChannelRequest;
import com.cdy.cdy.dto.request.UpdateStudyChannelRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.StudyChannelResponse;
import com.cdy.cdy.dto.response.study.DetailStudyChannelResponse;
import com.cdy.cdy.dto.response.study.GroupedStudiesResponse;
import com.cdy.cdy.dto.response.study.ResponseStudyByUser;
import com.cdy.cdy.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @Operation(summary = "유저의 전체 스터디목록 조회"
            , description = "로그인된 유저의 스터디 전체 목록을 반환")

    @GetMapping("/users/studies")
    public ResponseEntity<Page<ResponseStudyByUser>> findStudiesByUser
            (@AuthenticationPrincipal CustomUserDetails userDetails,
             @ParameterObject
             @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
             Pageable pageable) {
        Page<ResponseStudyByUser> list = studyService.findStudiesByUser(userDetails.getId(), pageable);

        return ResponseEntity.ok(list);

    }


    @Operation(
            summary = "스터디 글 생성",
            description = """
                    1) 먼저 `/storage/presign` API를 호출해 presigned URL을 발급받습니다.
                    2) 발급받은 URL로 이미지를 직접 업로드합니다.
                    3) 업로드가 끝나면, 응답으로 받은 이미지 `key` 값을 `CreateStudyChannelRequest`에 포함해 요청하세요.
                    """
    )
    //스터디 생성
    @PostMapping("/create")
    public ResponseEntity<StudyChannelResponse> createStudy(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CreateStudyChannelRequest request) {

        return ResponseEntity.ok(studyService.createStudy(user.getId(), request));
    }

    @Operation
            (summary = "스터디 단건 내용 조회",
            description = "스터디 이미지를 클릭하면 스터디 작성글의 내용 반환")
    // 단건 조회
    @GetMapping("/{studyId}")
    public ResponseEntity<StudyChannelResponse> getStudy(@PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.getStudy(studyId));
    }



    @Operation(
            summary = "카테고리별 스터디 채널 그룹 조회",
            description = """
    코딩 / 디자인 / 영상편집 3가지 카테고리를 한 번에 조회합니다.
    각 카테고리는 독립적으로 페이징 처리됩니다.
    
    - 요청 파라미터: codingPage, codingSize, designPage, designSize, videoPage, videoSize
    - 정렬 기준: createdAt DESC
    - 응답: GroupedStudiesResponse (카테고리별 Page<SimpleStudyDto>)
    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 조회 성공",
                    content = @Content(schema = @Schema(implementation = GroupedStudiesResponse.class)))
    })
    @GetMapping("/category/grouped")
    public ResponseEntity<GroupedStudiesResponse> getGrouped(
            @RequestParam(defaultValue = "0") int codingPage,
            @RequestParam(defaultValue = "10") int codingSize,
            @RequestParam(defaultValue = "0") int designPage,
            @RequestParam(defaultValue = "10") int designSize,
            @RequestParam(defaultValue = "0") int videoPage,
            @RequestParam(defaultValue = "10") int videoSize
    ) {
        Sort sort   = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable coding = PageRequest.of(codingPage, codingSize);
        Pageable design = PageRequest.of(designPage, designSize);
        Pageable video  = PageRequest.of(videoPage,  videoSize);

        return ResponseEntity.ok(
                studyService.getStudiesGrouped(coding, design, video)
        );
    }

    //스터디 삭제
    @Operation(
            summary = "스터디 삭제",
            description = "studyId를 받고 해당 스터디 삭제 해당 유저만 삭제가능"
    )
    @DeleteMapping("/delete/{studyId}")
    public ResponseEntity<Void> deleteStudy(@PathVariable Long studyId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        studyService.deleteStudy(studyId, userDetails.getId());
        return ResponseEntity.ok().build();

    }
    //스터디 수정
    @Operation
            (summary = "스터디 수정",
            description = "studyId와 updateStudyChannelRequest(내용,이미지)를 받고 이미지수정" +
                    "해당 유저만 수정가능")
    @PutMapping("/update/study/{studyId}")
    public ResponseEntity<String> updateStudy(@PathVariable Long studyId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody UpdateStudyChannelRequest rq) {

        studyService.updateStudy(studyId,userDetails.getId(),rq);
        return ResponseEntity.ok("스터디가 수정되었습니다.");
    }


    @Operation(summary = "유저의 상세스터디채널 반환", description = "유저의 id를 토대로 해당 유저의 상세스터디채널 반환")
    @GetMapping("/user/{userId}")
    public ResponseEntity<DetailStudyChannelResponse> getStudyChannel(@PathVariable Long userId,
               @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
    Pageable StudyPageable) {
        DetailStudyChannelResponse studyChannel = studyService.findStudyChannel(userId, StudyPageable);
        return ResponseEntity.ok(studyChannel);

    }

}





