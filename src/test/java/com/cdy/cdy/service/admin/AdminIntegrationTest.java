package com.cdy.cdy.service.admin;

import com.cdy.cdy.CdyApplication;
import com.cdy.cdy.controller.admin.AdminController;
import com.cdy.cdy.dto.admin.BannerResponseDto;
import com.cdy.cdy.dto.admin.CreateBanner;
import com.cdy.cdy.dto.admin.CursorResponse;
import com.cdy.cdy.dto.admin.UserInfoResponse;
import com.cdy.cdy.dto.response.project.SingleProjectResponse;
import com.cdy.cdy.entity.Banner;
import com.cdy.cdy.entity.Partner;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.entity.project.Project;
import com.cdy.cdy.exception.GlobalExceptionHandler;
import com.cdy.cdy.repository.BannerRepository;
import com.cdy.cdy.repository.PartnerRepository;
import com.cdy.cdy.repository.ProjectRepository;
import com.cdy.cdy.repository.UserRepository;
import com.cdy.cdy.service.ImageUrlResolver;
import com.cdy.cdy.service.R2StorageService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;



import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = CdyApplication.class)
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class AdminIntegrationTest {


    @Autowired
    MockMvc mockMvc;

    // 🔥 진짜로 사용할 repository (JPA 테스트용)
    @Autowired
    ProjectRepository projectRepository;


    @Autowired
    BannerRepository bannerRepository;

    @Autowired
    PartnerRepository partnerRepository;

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

//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
//                .setControllerAdvice(GlobalExceptionHandler.class) // @RestControllerAdvice 등록된 부분
//                .build();
//    }


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


    @Test
    void CreateBanner_DTO받고_배너생성() {

        //given
        CreateBanner createBanner = new CreateBanner();
        createBanner.setLink("youtube.com");
        createBanner.setImageKey("imageKey");
        //when&then
        adminService.addBanner(createBanner);

        // then
        List<Banner> list = bannerRepository.findAll();
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getLink()).isEqualTo("youtube.com");
        assertThat(list.get(0).getImageKey()).isEqualTo("imageKey");


    }

    @Test
    void 배너삭제() {


        //given

        Long bannerID = 1L;

        for (int i = 0; i < 2; i++) {
            Banner banner = Banner.builder()
                    .imageKey("imagekey")
                    .link("link")
                    .build();

            bannerRepository.save(banner);
        }

        //when

        adminService.deleteBanner(bannerID);
        List<BannerResponseDto> result = adminService.findAllBanner();

        //then

        assertThat(result).hasSize(1);


    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void 배너삭제컨트롤러_200반환() throws Exception {


        //given: 삭제 대상 배너 하나 저장
        Banner banner = bannerRepository.save(
                Banner.builder()
                        .imageKey("image")
                        .link("link")
                        .build()
        );

        bannerRepository.save(banner);

        Long id = banner.getId();

        //when & then
        mockMvc.perform(delete("/api/admin/deleteBanner/" + 1)
                        .header("Authorization", "Bearer test-token")) // 필요하면 추가
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string("배너가 삭제되었습니다 id : " + id));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 배너삭제컨트롤_존재하지않는아이디면_404반환() throws Exception {

        // given
        Long notExistId = 9999L;   // 존재할 수 없는 ID 만듦


        // when & then
        mockMvc.perform(delete("/api/admin/deleteBanner/" + notExistId))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void 파트너_삭제_200_반환() throws Exception {

        //given

        Partner partner = Partner.builder()
                .link("link")
                .name("기업")
                .imageKey("djqtasd")
                .build();
        partnerRepository.save(partner);

        //when && then

        mockMvc.perform(delete("/api/admin/deletePartner/" + partner.getId()))
                .andExpect(status().isOk());


    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 파트너_삭제_존재하지않는_id면_404반환() throws Exception {


        //when & then

        mockMvc.perform(
                        delete("/api/admin/deletePartner/" + 999)
                )
                .andExpect(status().is(404));

    }


    @Test
    void 첫페이지요청시_최신ID가_반드시_조회된다() {
        // given — 유저 3명 생성 (ID 자동 증가: 1,2,3)
        User u1 = userRepository.save(User.builder()
                .name("A").email("a@test.com").passwordHash("pw").build());
        User u2 = userRepository.save(User.builder()
                .name("B").email("b@test.com").passwordHash("pw").build());
        User u3 = userRepository.save(User.builder()
                .name("C").email("c@test.com").passwordHash("pw").build()); // 최신

        Long maxId = u3.getId(); // 최신 ID (예: 3)

        // when — lastUserId = null → 내부에서 maxId + 1 로 조회됨
        CursorResponse<UserInfoResponse> response =
                adminService.getUserInfoList(null, 10);

        List<UserInfoResponse> data = response.getData();

        // then — 최신 ID가 포함되어 있어야 정상
        boolean containsLatest = data.stream()
                .anyMatch(dto -> dto.getId().equals(maxId));

        assertThat(containsLatest)
                .as("첫 페이지에는 최신 유저(ID=%s)가 반드시 포함되어야 한다.", maxId)
                .isTrue();
    }
    }

