package com.cdy.cdy.service;

import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AuthSecurityTest {


    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    R2StorageService r2StorageService;

    @MockitoBean
    S3Presigner s3Presigner;

    @MockitoBean
    S3Client s3Client;

    @MockitoBean
    JavaMailSender javaMailSender;


    @Test
    void 인증없이_보호된_API_요청하면_401() throws Exception {
        mockMvc.perform(get("/api/attendance/calendar"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void JWT_토큰_있으면_정상_접근() throws Exception {


        String jwt = jwtUtil.createJwt(1L, "asdfadfs", UserRole.USER.toString(), 3600);

        mockMvc.perform(get("/api/attendance/calendar")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }

}
