package com.cdy.cdy.service.admin;


import com.cdy.cdy.dto.admin.*;
import com.cdy.cdy.dto.request.LoginRequest;
import com.cdy.cdy.dto.request.SignUpRequest;
import com.cdy.cdy.dto.response.LoginResponse;
import com.cdy.cdy.dto.response.project.AdminProjectResponse;
import com.cdy.cdy.dto.response.project.SingleProjectResponse;
import com.cdy.cdy.dto.response.study.AdminStudyResponse;
import com.cdy.cdy.entity.*;
import com.cdy.cdy.entity.project.Project;
import com.cdy.cdy.repository.*;
import com.cdy.cdy.service.AuthService;
import com.cdy.cdy.service.ImageUrlResolver;
import com.cdy.cdy.service.MailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudyChannelRepository studyChannelRepository;
    private final ProjectRepository projectRepository;
    private final MailService mailService;
    private final BannerRepository bannerRepository;
    private final ImageUrlResolver imageUrlResolver;
    private final AuthService authService;
    private final PartnerRepository partnerRepository;


    //관리자용 아이디 생성
    @Transactional
    public void createdAdmin(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .nickname(signUpRequest.getNickname())
                .email(signUpRequest.getEmail())
                .name(signUpRequest.getName())
                .passwordHash(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(UserRole.ADMIN) // 필요 시 지정, 아니면 of()로 기본값 처리
                .phoneNumber(signUpRequest.getPhoneNumber())
                .category(UserCategory.from(signUpRequest.getUserCategory()))
                .build();
        userRepository.save(user);
    }

    //홈화면 데이터 조회
    @Transactional(readOnly = true)
    public CursorResponse<AdminHomeResponseDto> getHomeData(Long lastUserId, int limit) {

        List<AdminHomeResponseDto> data = userRepository.findHomeData(lastUserId, limit);

        Long nextCursor = data.isEmpty() ? null : data.get(data.size() - 1).getId();
        boolean hasNext = data.size() == limit;

        return new CursorResponse<>(data, nextCursor, hasNext);
    }

    //특정 유저 오프라인 참가횟수 수정
    @Transactional
    public void updateOffline(Long count, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));


        user.changeOffline(count);
    }
    //전체 스터디 목록 조회
    public Page<AdminStudyResponse> findStudyList(Pageable pageable) {

        Page<AdminStudyResponse> studyList = studyChannelRepository.findStudyList(pageable);

        return studyList;
    }

    //전체 프로젝트 목록 조회
    public Page<AdminProjectResponse> findProjectList(Pageable pageable) {

        Page<AdminProjectResponse> list = projectRepository.findProjectforAdminPage(pageable);


        Page<AdminProjectResponse> resolvedList = list.map(project ->
                AdminProjectResponse.builder()

                        .id(project.getId())
                        .ProjectImageUrl(imageUrlResolver.toPresignedUrl(project.getProjectImageUrl()))
                        .build()
        );


        return resolvedList;


    }

    public SingleProjectResponse getSingleProject(Long id) {


        Project project = projectRepository.findProjectWithFirstImageByProjectId(id)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트를 찾을 수 없습니다. 프로젝트 id : " + id));

        SingleProjectResponse dto = SingleProjectResponse.builder()
                .id(project.getId())
                .imageUrl(imageUrlResolver.toPresignedUrl(project.getLogoImageKey()))
                .content(project.getDescription())
                .build();


        return dto;
    }





    //스터디 삭제
    @Transactional
    public void deleteStudy(DeleteStudyReason request) {


        studyChannelRepository.findById(request.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("존재하지 않는 스터디 아이디입니다 id: " + request.getId()));

        User user = userRepository.findByStudyID(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다 . id"));


        mailService.sendMail(user.getEmail(),"스터디가 삭제되었습니다.", request.getReason());

        studyChannelRepository.deleteById(request.getId());


    }

    //유저목록 조회 이름,이메일,전화번호,비밀번호해쉬,가입일자,포지션
    public CursorResponse<UserInfoResponse> getUserInfoList(Long lastUserId, int limit) {


        if (lastUserId == null) {
            lastUserId = userRepository.findMaxId();
        }

        if (lastUserId == null) {
            return new CursorResponse<>(List.of(), null, false);
        }

        List<UserInfoResponse> users = userRepository.getUserInfoList(lastUserId, limit);

        boolean hasNext = users.size() == limit;

        Long nextCursor = users.isEmpty() ? null : users.get(users.size() - 1).getId();// 단순 예시

        CursorResponse<UserInfoResponse> response = new CursorResponse<>(users, nextCursor, hasNext);

        return response;
    }
    //배너추가
    @Transactional
    public void addBanner(String imageKey) {

        if (imageKey == null || imageKey.isEmpty()) {
            throw new IllegalStateException("이미지 키가 유효하지 않습니다.");
        }

        Banner banner = Banner.builder()
                .imageKey(imageKey)
                .build();
        bannerRepository.save(banner);

    }


    //배너 전체 조회
    public List<BannerResponseDto> findAllBanner() {

        List<Banner> banners = bannerRepository.findAll();

        List<BannerResponseDto> dto = banners.stream().map(banner ->
                BannerResponseDto.builder()
                        .id(banner.getId())
                        .imageUrl(imageUrlResolver.toPresignedUrl(banner.getImageKey()))
                        .build()

        ).toList();

        return dto;

    }

    //특정 배너 조회
    public BannerResponseDto findOneBanner(Long bannerId) {

        Banner banner = bannerRepository.findById(bannerId).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않는 배너 아이디입니다 id : " + bannerId));

        String imageKey = banner.getImageKey();

        BannerResponseDto dto = BannerResponseDto.builder()
                .id(bannerId)
                .imageUrl(imageUrlResolver.toPresignedUrl(imageKey))
                .build();

        return dto;
    }


    //관리자 로그인
    public LoginResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));

        if (user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("관리자 계정이 아닙니다.");
        }

        return authService.login(loginRequest);
    }

    //파트너 추가
    @Transactional
    public void AddPartner(CreatePartner createPartner) {


        Partner partner = Partner.builder()
                .name(createPartner.getName())
                .imageKey(createPartner.getImageKey())
                .build();
        partnerRepository.save(partner);
    }


    //파트너 전체 조회
    public List<PartnerResponseDto> findAllPartners() {

        List<Partner> all = partnerRepository.findAll();


        List<PartnerResponseDto> list = all.stream().map(partner ->
                PartnerResponseDto.builder()
                        .id(partner.getId())
                        .imageUrl(imageUrlResolver.toPresignedUrl(partner.getImageKey()))
                        .name(partner.getName())
                        .build()
        ).toList();
        return list;
    }

    //파트너 단건 조회
    public PartnerResponseDto findOnePartner(Long id) {

        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("파트너를 찾을 수 없습니다 id : " + id));

        PartnerResponseDto dto = PartnerResponseDto.builder()
                .id(partner.getId())
                .name(partner.getName())
                .imageUrl(imageUrlResolver.toPresignedUrl(partner.getImageKey()))
                .build();

        return dto;
    }

}
