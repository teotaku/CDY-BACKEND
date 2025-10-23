package com.cdy.cdy.service.admin;


import com.cdy.cdy.dto.admin.AdminHomeResponseDto;
import com.cdy.cdy.dto.admin.CursorResponse;
import com.cdy.cdy.dto.request.SignUpRequest;
import com.cdy.cdy.dto.response.project.AdminProjectResponse;
import com.cdy.cdy.dto.response.study.AdminStudyResponse;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserCategory;
import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.repository.ProjectRepository;
import com.cdy.cdy.repository.StudyChannelRepository;
import com.cdy.cdy.repository.UserRepository;
import com.cdy.cdy.service.ImageUrlResolver;
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
    private ImageUrlResolver imageUrlResolver;

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

    //오프라인 참가횟수 수정
    @Transactional
    public void updateOffline(Long count, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));


        user.changeOffline(count);
    }

    public Page<AdminStudyResponse> findStudyList(Pageable pageable) {

        Page<AdminStudyResponse> studyList = studyChannelRepository.findStudyList(pageable);

        return studyList;
    }


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
}
