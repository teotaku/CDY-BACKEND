package com.cdy.cdy.service;

import com.cdy.cdy.dto.request.UpdateMyImage;
import com.cdy.cdy.dto.response.project.ProjectResponse;
import com.cdy.cdy.dto.response.StudyChannelResponse;
import com.cdy.cdy.entity.Project;
import com.cdy.cdy.entity.ProjectMember;
import com.cdy.cdy.entity.StudyChannel;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.repository.ProjectMemberRepository;
import com.cdy.cdy.repository.StudyChannelRepository;
import com.cdy.cdy.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 이미 SecurityConfig에 Bean 등록돼 있죠
    private final ProjectMemberRepository projectMemberRepository;
    private final StudyChannelRepository studyChannelRepository;

    @Operation(
            summary = "내 프로필 이미지 변경",
            description = """
    1) `/storage/presign` API로 presigned URL 발급 → 프론트에서 업로드 완료
    2) 업로드 후 반환된 `imageKey`를 이 API에 전달하면, 로그인된 유저의 아바타가 교체됩니다.
    """
    )
    @Transactional
    public void changeImage(Long userId, UpdateMyImage updateMyImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        user.changeAvatar(updateMyImage.getImageKey());
        userRepository.save(user);

    }


    @Transactional
    public void changeNickname(Long userId, String newNickname) {
        User u = userRepository.findById(userId).orElseThrow();
        if (userRepository.existsByNicknameAndIdNot(newNickname, userId)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임");
        }
        u.changeNickname(newNickname); // 엔티티에 이미 메서드 있음
    }

    @Transactional
    public void changeEmail(Long userId, String newEmail) {
        User u = userRepository.findById(userId).orElseThrow();
        if (userRepository.existsByEmailAndIdNot(newEmail, userId)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일");
        }
        u.changeEmail(newEmail); // 없으면 동일 패턴으로 추가
        // 이메일을 username으로 쓰는 프로젝트라면: 토큰 재발급/재로그인 권장
    }

    @Transactional
    public void changePassword(Long userId, String currentPw, String newPw) {
        User u = userRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(currentPw, u.getPasswordHash())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않음");
        }
        u.changePasswordHash(passwordEncoder.encode(newPw)); // 엔티티 메서드 활용
    }

    // 상태 컬럼이 없을 때(최근 1건)
    public ProjectResponse getLatestProject(Long userId) {
        ProjectMember pm = projectMemberRepository
                .findFirstByUser_IdOrderByJoinedAtDesc(userId)
                .orElseThrow(() -> new IllegalStateException("참여 중인 프로젝트가 없습니다."));

        Project p = pm.getProject();
        String phoneNumber = p.getManager().getPhoneNumber();
        int size = p.getProjectMembers().size();


        return ProjectResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .imageUrl(p.getLogoImageKey())
                .leaderId(p.getManager().getId())
                .positions(Collections.emptyList())
                .techs(Collections.emptyList())
                .questions(Collections.emptyList())
                .memberCount(size)
                .kakaoLink(phoneNumber)
                .build();
    }

    // 2. 단건 조회
    public StudyChannelResponse getStudy(Long studyId) {
        StudyChannel studyChannel = studyChannelRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디 채널을 찾을 수 없습니다."));

        return StudyChannelResponse.from(studyChannel);
    }

    // 3. 전체 조회
    public List<StudyChannelResponse> getAllStudies() {
        List<StudyChannel> studies = studyChannelRepository.findAll();

        return studies.stream()
                .map(StudyChannelResponse::from)
                .toList();
    }


}
