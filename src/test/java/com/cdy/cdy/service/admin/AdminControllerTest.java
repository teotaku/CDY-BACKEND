package com.cdy.cdy.service.admin;

import com.cdy.cdy.controller.admin.AdminController;
import com.cdy.cdy.dto.admin.CursorResponse;
import com.cdy.cdy.dto.admin.DeleteStudyReason;
import com.cdy.cdy.dto.admin.UserInfoResponse;
import com.cdy.cdy.dto.request.LoginRequest;
import com.cdy.cdy.dto.response.study.AdminStudyResponse;
import com.cdy.cdy.dto.response.study.StudyChannelResponse;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.repository.UserRepository;
import com.cdy.cdy.service.AuthService;
import com.cdy.cdy.service.StudyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    private static final Logger log = LoggerFactory.getLogger(AdminControllerTest.class);
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JavaMailSender mailSender;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private StudyService studyService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 스터디 목록 조회 성공")
    void findStudyList() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<AdminStudyResponse> content = List.of(
                new AdminStudyResponse(1L, "스터디 제목1", LocalDateTime.now()),
                new AdminStudyResponse(2L, "스터디 제목2", LocalDateTime.now())
        );
        Page<AdminStudyResponse> page = new PageImpl<>(content, pageable, content.size());

        given(adminService.findStudyList(any(Pageable.class))).willReturn(page);

        // when & then
        mockMvc.perform(get("/api/admin/findStudyList")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("스터디 제목1"))
                .andExpect(jsonPath("$.content[1].content").value("스터디 제목2"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 스터디 단건 조회 성공")
    void 스터디_단건_조회_성공_200() throws Exception {


        //given

        StudyChannelResponse dto = StudyChannelResponse.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .images(List.of())
                .content("테스트")
                .build();

        given(studyService.getStudy(1L)).willReturn(dto);

        //when & then


        mockMvc.perform
                        (get("/api/admin/findStudy/" + 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("테스트"));


    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("관리자 권한 없는 유저가 관리자페이지 스터디삭제하면 권한에러 403")
    void 관리자_권한_없는_유저가_관리자페이지_스터디삭제하면_403() throws Exception {

        //given
        StudyChannelResponse dto = StudyChannelResponse.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .images(List.of())
                .content("테스트")
                .build();


        //when & then

        mockMvc.perform(delete("/api/admin/deleteStudy/" + 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 관리자계정_유저목록_조회_커서형태() throws Exception {
        // given
        List<UserInfoResponse> mockData = List.of(
                new UserInfoResponse() {
                    public Long getId() { return 1L; }
                    public String getName() { return "테오"; }
                    public String getPhoneNumber() { return "010-1234-5678"; }
                    public String getEmail() { return "teo@example.com"; }
                    public String getPasswordHash() { return "hash123"; }
                    public String getCategory() { return "ADMIN"; }
                    public LocalDateTime getCreatedAt() { return LocalDateTime.now(); }
                }
        );


        CursorResponse<UserInfoResponse> mockResponse =
                new CursorResponse<>(mockData, 100L, false);

        given(adminService.getUserInfoList(anyLong(), anyInt()))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/admin/getUserInfoList")
                        .param("lastUserId", "999")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("테오"))        // ✅ 핵심 수정
                .andExpect(jsonPath("$.data[0].email").value("teo@example.com"))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andDo(print());



    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 스터디를 삭제하면 메일 전송 메서드가 호출된다")
    void 관리자_스터디삭제_메일전송검증() throws Exception {

        // given
        DeleteStudyReason request = new DeleteStudyReason();
        request.setId(1L);
        request.setReason("운영정책 위반");
        // when
        mockMvc.perform(delete("/api/admin/deleteStudy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content("""
                            {
                                "id": 1,
                                "reason": "운영정책 위반"
                            }
                        """))
                .andExpect(status().isOk());
        // then
        verify(adminService, times(1)).deleteStudy(any(DeleteStudyReason.class)); // ✅ AdminService 호출 확인
    }

    @Test
    void 로그인성공_200반환() throws Exception {
        // given
        LoginRequest req = new LoginRequest("admin@test.com", "1234");

        // adminService.login() 이 예외를 던지지 않으면 성공이라고 가정
        doNothing().when(adminService).login(any(LoginRequest.class));

        // when & then
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("관리자 로그인 성공"));

        // 서비스가 실제로 호출됐는지 검증
        verify(adminService, times(1)).login(any(LoginRequest.class));
    }

    // -----------------------------------------
    // 2) 관리자 아님 → 400 or 403 예외 발생
    // -----------------------------------------
    @Test
    void 관리자아님_예외발생() throws Exception {
        // given
        LoginRequest req = new LoginRequest("user@test.com", "1234");

        doThrow(new IllegalArgumentException("관리자 계정이 아닙니다."))
                .when(adminService).login(any(LoginRequest.class));

        // when & then
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자 계정 - 유저목록 조회 - 커서 기반 정상 응답")
    void 관리자계정_유저목록_조회_커서정상상태() throws Exception {

        // mock 데이터 준비
        List<UserInfoResponse> mockData = List.of(
                new UserInfoResponse() {
                    @Override
                    public Long getId() {
                        return 10L;
                    }

                    @Override
                    public String getName() {
                        return "테오";
                    }

                    @Override
                    public String getPhoneNumber() {
                        return "010-1234-5678";
                    }

                    @Override
                    public String getEmail() {
                        return "teo@example.com";
                    }

                    @Override
                    public String getPasswordHash() {
                        return "hash123";
                    }

                    @Override
                    public String getCategory() {
                        return "ADMIN";
                    }

                    @Override
                    public LocalDateTime getCreatedAt() {
                        return LocalDateTime.now();
                    }
                }
        );

        CursorResponse<UserInfoResponse> mockResponse =
                new CursorResponse<>(mockData, 9L, true);

        given(adminService.getUserInfoList(100L, 10))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/admin/getUserInfoList")
                        .param("lastUserId", "100")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(10))
                .andExpect(jsonPath("$.data[0].name").value("테오"))
                .andExpect(jsonPath("$.data[0].email").value("teo@example.com"))
                .andExpect(jsonPath("$.data[0].category").value("ADMIN"))
                .andExpect(jsonPath("$.nextCursor").value(9))
                .andExpect(jsonPath("$.hasNext").value(true));
    }

}
