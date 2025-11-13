package com.cdy.cdy.service.admin;


import com.cdy.cdy.dto.admin.AdminHomeResponseDto;
import com.cdy.cdy.dto.admin.CursorResponse;
import com.cdy.cdy.dto.admin.DeleteStudyReason;
import com.cdy.cdy.dto.admin.UserInfoResponse;
import com.cdy.cdy.dto.request.LoginRequest;
import com.cdy.cdy.dto.request.SignUpRequest;
import com.cdy.cdy.dto.response.project.AdminProjectResponse;
import com.cdy.cdy.dto.response.study.AdminStudyResponse;
import com.cdy.cdy.entity.Banner;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserCategory;
import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.repository.BannerRepository;
import com.cdy.cdy.repository.ProjectRepository;
import com.cdy.cdy.repository.StudyChannelRepository;
import com.cdy.cdy.repository.UserRepository;
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
    private ImageUrlResolver imageUrlResolver;
    private final AuthService authService;

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


        List<UserInfoResponse> users = userRepository.getUserInfoList(lastUserId, limit);

        boolean hasNext = users.size() == limit;

        Long nextCursor = users.isEmpty() ? null : lastUserId + limit; // 단순 예시

        CursorResponse<UserInfoResponse> response = new CursorResponse<>(users, nextCursor, hasNext);

        return response;
    }
    //배너추가
    public void addBanner(String imageKey) {

        if (imageKey == null || imageKey.isEmpty()) {
            throw new IllegalStateException("이미지 키가 유효하지 않습니다.");
        }

        Banner banner = Banner.builder()
                .imageKey(imageKey)
                .build();
        bannerRepository.save(banner);

    }

    public void login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));

        if (user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("관리자 계정이 아닙니다.");
        }

        authService.login(loginRequest);
    }
}
