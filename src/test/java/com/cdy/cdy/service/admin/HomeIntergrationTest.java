package com.cdy.cdy.service.admin;


import com.cdy.cdy.CdyApplication;
import com.cdy.cdy.dto.admin.CreateBanner;
import com.cdy.cdy.dto.response.HomeBannerResponseDto;
import com.cdy.cdy.dto.response.HomePartnerResponseDto;
import com.cdy.cdy.entity.Banner;
import com.cdy.cdy.entity.Partner;
import com.cdy.cdy.repository.BannerRepository;
import com.cdy.cdy.repository.PartnerRepository;
import com.cdy.cdy.service.HomeService;
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
import software.amazon.awssdk.services.s3.model.Part;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(classes = CdyApplication.class)
@Transactional
public class HomeIntergrationTest {

    @Autowired
    BannerRepository bannerRepository;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    HomeService homeService;

    @MockitoBean
    R2StorageService r2StorageService;

    @MockitoBean
    S3Presigner s3Presigner;

    @MockitoBean
    S3Client s3Client;

    @MockitoBean
    ImageUrlResolver imageUrlResolver;


    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    void 홈화면_배너전체_조회() {


        //given
        for (int i = 0; i < 3; i++) {

            Banner banner = Banner.builder()
                    .imageKey("imagekey" + i)
                    .link("link" + i)
                    .build();
            bannerRepository.save(banner);
        }

        //when

        List<HomeBannerResponseDto> list = homeService.findBanners();


        //then
        assertThat(list).hasSize(3);


    }

    @Test
    void 파트너_전체_조회_홈화면() {



        //given
        for (int i = 0; i < 3; i++) {

            Partner partner = Partner.builder()
                    .imageKey("imagekey" + i)
                    .link("link" + i)
                    .build();
            partnerRepository.save(partner);
        }

        //when

        List<HomePartnerResponseDto> list = homeService.findPartners();



        //then

        assertThat(list).hasSize(3);

    }

}

