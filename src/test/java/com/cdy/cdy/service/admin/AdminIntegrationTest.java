package com.cdy.cdy.service.admin;

import com.cdy.cdy.CdyApplication;
import com.cdy.cdy.controller.admin.AdminController;
import com.cdy.cdy.dto.admin.BannerResponseDto;
import com.cdy.cdy.dto.response.project.SingleProjectResponse;
import com.cdy.cdy.entity.Banner;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.entity.project.Project;
import com.cdy.cdy.repository.BannerRepository;
import com.cdy.cdy.repository.ProjectRepository;
import com.cdy.cdy.repository.UserRepository;
import com.cdy.cdy.service.ImageUrlResolver;
import com.cdy.cdy.service.R2StorageService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;



import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = CdyApplication.class)
@Transactional
class AdminIntegrationTest {

    // 🔥 진짜로 사용할 repository (JPA 테스트용)
    @Autowired
    ProjectRepository projectRepository;


    @Autowired
    BannerRepository bannerRepository;

    // 🔥 테스트할 대상
    @Autowired
    AdminService adminService;

    @Autowired
    AdminController adminController;

    @Autowired
    UserRepository userRepository;
    // 🔥 나머지 죄다 MOCK
//    @MockitoBean
//    PasswordEncoder passwordEncoder;
//    @MockitoBean
//    StudyChannelRepository studyChannelRepository;
//    @MockitoBean
//    MailService mailService;

    @MockitoBean
    JavaMailSender javaMailSender;
//    @MockitoBean
//    BannerRepository bannerRepository;
    @MockitoBean
    ImageUrlResolver imageUrlResolver;
//    @MockitoBean
//    AuthService authService;

    @MockitoBean
    R2StorageService r2StorageService;

    @MockitoBean
    S3Presigner s3Presigner;

    @MockitoBean
    S3Client s3Client;



    @Test
    void findSingleProject_success() {

        // 1) 테스트용 유저 저장 (프로젝트 매니저)
        User user = User.builder()
                .name("테스트유저")
                .phoneNumber("010-1234-5678")
                .email("test@test.com")
                .passwordHash("test-password")  // ★ 필수
                .role(UserRole.USER)
                .build();
        userRepository.save(user);

        // 2) 테스트용 프로젝트 저장
        Project project = Project.builder()
                .title("테스트 프로젝트")
                .description("설명")
                .capacity(10)
                .manager(user)
                .logoImageKey("key")
                .kakaoLink("test-link")
                .build();
        projectRepository.save(project);
        // 3) 서비스 호출
        SingleProjectResponse result =
                adminService.getSingleProject(project.getId());
        // 4) 검증
        assertThat(result.getContent()).isEqualTo("설명");
    }



    @Test
    void findAllBanner_success() {

        // given - DB에 배너 2개 저장
        Banner banner1 = Banner.builder()
                .imageKey("key1")
                .build();
        bannerRepository.save(banner1);

        Banner banner2 = Banner.builder()
                .imageKey("key2")
                .build();
        bannerRepository.save(banner2);

        // when
        List<BannerResponseDto> result = adminService.findAllBanner();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isNotNull();
        assertThat(result.get(0).getImageUrl())
                .isEqualTo(imageUrlResolver.toPresignedUrl("key1"));
    }


    @Test
    void findOneBanner_success() {

        // given
        Banner banner = Banner.builder()
                .imageKey("key123")
                .build();

        bannerRepository.save(banner);

        // when
        BannerResponseDto result = adminService.findOneBanner(banner.getId());

        // then
        assertThat(result.getId()).isEqualTo(banner.getId());
        assertThat(result.getImageUrl())
                .isEqualTo(imageUrlResolver.toPresignedUrl("key123"));
    }
}