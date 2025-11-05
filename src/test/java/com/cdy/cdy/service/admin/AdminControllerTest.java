package com.cdy.cdy.service.admin;

import com.cdy.cdy.controller.admin.AdminController;
import com.cdy.cdy.dto.response.study.AdminStudyResponse;
import com.cdy.cdy.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private AuthService authService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자가 스터디 목록 조회 성공")
    void findStudyList() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<AdminStudyResponse> content = List.of(
                new AdminStudyResponse(1L, "스터디 제목1",  LocalDateTime.now()),
                new AdminStudyResponse(2L, "스터디 제목2",  LocalDateTime.now())
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




    }
