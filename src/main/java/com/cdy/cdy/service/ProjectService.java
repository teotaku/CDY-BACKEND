package com.cdy.cdy.service;


import com.cdy.cdy.dto.request.ApplyProjectRequest;
import com.cdy.cdy.dto.request.CreateProjectRequest;
import com.cdy.cdy.dto.response.MemberBrief;
import com.cdy.cdy.dto.response.project.*;
import com.cdy.cdy.entity.*;
import com.cdy.cdy.entity.project.*;
import com.cdy.cdy.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService {


    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectQuestionRepository projectQuestionRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectTechRepository projectTechRepository;
    private final ProjectAnswerRepository projectAnswerRepository;
    private final TechTagRepository techTagRepository;
    private final R2StorageService r2StorageService;
    private final ImageUrlResolver imageUrlResolver;


    //프로젝트 생성
    public void createProject(Long leaderUserId,
                              CreateProjectRequest req) {

        boolean existsAny = projectMemberRepository.existsByUser_IdAndStatusIn(
                leaderUserId,
                List.of(ProjectMemberStatus.APPLIED, ProjectMemberStatus.APPROVED)
        );
        if (existsAny) {
            throw new IllegalStateException("다른 프로젝트에 이미 신청/참여 중입니다.");
        }


        // 1) 유저 조회
        User leader = userRepository.findById(leaderUserId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));


        // 2) 프로젝트 생성
        Project project = Project.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .capacity(req.getCapacity())
                .manager(leader)
                .positions(req.getPositions())
                .slogan(req.getSlogan())
                .status(ProjectStatus.IN_PROGRESS)
                .logoImageKey(req.getImageKey()) // null 가능
                .kakaoLink(req.getKakaoLink())
                .build();
        projectRepository.save(project);

        // 3) 리더 등록, 프로젝트 상태 진행중 초기화
        ProjectMember leaderMember = ProjectMember.builder()
                .project(project)
                .user(leader)
                .role(ProjectMemberRole.LEADER)
                .status(ProjectMemberStatus.APPROVED)
                .build();
        projectMemberRepository.save(leaderMember);


        for (String name : req.getTechs()) {
            if (name == null || name.isBlank()) continue;

            TechTag tag = techTagRepository.findByName(name)
                    .orElseGet(() -> techTagRepository.save(TechTag.builder().name(name).build()));

            projectTechRepository.save(ProjectTech.link(project, tag));
            // 또는: ProjectTech.builder().project(project).techTag(tag).build()
        }
        // 2. 질문 저장 (List<String> → ProjectQuestion 엔티티)
        List<String> questions = req.getQuestions(); // ["자기소개", "가능 요일", "기술"]
        for (int i = 0; i < questions.size(); i++) {
            projectQuestionRepository.save(
                    ProjectQuestion.builder()
                            .project(project)
                            .questionText(questions.get(i)) // 질문 내용
                            .displayOrder(i + 1)            // 순서 보장
                            .build()
            );
        }

        String phone = leader.getPhoneNumber(); // 또는 req.getContact()
    }

    //진행중 프로젝트
    public ProgressingProjectResponse getProgressingProject(Long userId) {

        Project project = projectRepository.findProgressingProjectByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("진행중인 프로젝트가 없습니다."));

//        ProjectMember pm = projectMemberRepository
//                .findTopByUserIdAndStatusOrderByJoinedAtDesc(userId, ProjectMemberStatus.APPROVED)
//                .orElseThrow(() -> new EntityNotFoundException("신청중인 프로젝트가 없습니다."));


        long memberCount = projectMemberRepository.countByApprovedPm(project.getId());

        List<ProjectMember> members = projectMemberRepository
                .findApprovedMembersWithUserByProjectId(project.getId());


        List<MemberBrief> memberBriefs = members.stream()
                .map(pm -> MemberBrief.builder()
                        .userId(pm.getUser().getId())
                        .name(pm.getUser().getNickname())
                        .profileKey(imageUrlResolver.toPresignedUrl(pm.getUser().getProfileImageKey()))
                        .build()).toList();


        return ProgressingProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .capacity(project.getCapacity())
                .memberCount(memberCount)
                .memberBriefs(memberBriefs)
                .position(project.getPositions())
                .kakaoLink(project.getKakaoLink())
                .imageKey(imageUrlResolver.toPresignedUrl(project.getLogoImageKey()))
                .build();


    }

    //신청중 프로젝트
    public ApplyingProjectResponse getApplyProject(Long userId) {

        ProjectMember pm = projectMemberRepository
                .findTopByUserIdAndStatusOrderByJoinedAtDesc(userId, ProjectMemberStatus.APPLIED)
                .orElseThrow(() -> new EntityNotFoundException("신청중인 프로젝트가 없습니다."));

        Project project = pm.getProject();
        long memberCount = projectMemberRepository.countByApprovedPm(project.getId());


        List<ProjectMember> members = projectMemberRepository
                .findApprovedMembersWithUserByProjectId(project.getId());

        List<MemberBrief> memberBriefs = members.stream()
                .map(pmb -> MemberBrief.builder()
                        .userId(pmb.getUser().getId())
                        .name(pmb.getUser().getNickname())
                        .profileKey(imageUrlResolver.toPresignedUrl(pm.getUser().getProfileImageKey()))
                        .build()).toList();


        return ApplyingProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .memberBriefs(memberBriefs)
                .position(project.getPositions())
                .memberCount(memberCount)
                .imageKey(imageUrlResolver.toPresignedUrl(project.getLogoImageKey()))
                .techs(project.getTechs())
                .build();
    }


    //프로젝트 신청
    @Transactional
    public void applyToProject(Long userId, Long projectId,
                               ApplyProjectRequest req) {

        // 1) 참조 로딩 (존재 검증)
        User user = userRepository.getReferenceById(userId); // 존재 보장 시 getReferenceById OK
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트가 없습니다."));

        // 2) 자기 프로젝트에 신청 금지
        if (project.getManager().getId().equals(userId)) {
            throw new IllegalStateException("본인이 관리자인 프로젝트에는 신청할 수 없습니다.");
        }

        //이미 완료된 프로젝트 신청금지
        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 프로젝트입니다.");
        }


        // 3) 중복 신청/참여 금지 (이미 신청 or 이미 승인 상태면 막기)
        boolean already = projectMemberRepository.existsByUser_IdAndProject_IdAndStatusIn(
                userId, projectId, List.of(ProjectMemberStatus.APPLIED, ProjectMemberStatus.APPROVED));
        if (already) {
            throw new IllegalStateException("이미 신청 또는 참여 중인 프로젝트입니다.");
        }

        boolean existsAny = projectMemberRepository
                .existsByUser_IdAndStatusIn(
                        userId,
                        List.of(ProjectMemberStatus.APPLIED, ProjectMemberStatus.APPROVED)
                );
        if (existsAny) throw new IllegalStateException("다른 프로젝트에 이미 신청/참여 중입니다.");

        // 4) 정원 체크 (정책 선택)
        if (project.getCapacity() != null) {
            long approvedCount = projectMemberRepository.countByProject_IdAndStatus(
                    projectId, ProjectMemberStatus.APPROVED);
            if (approvedCount >= project.getCapacity()) {
                // (A) 대기자 없이 꽉 차면 차단:
                // throw new IllegalStateException("정원이 가득 찼습니다.");
                // (B) 대기자(신청) 허용 → 그대로 진행 (여기선 B로 진행)
            }
        }

        // 5) 신청 엔티티 생성 (status=APPLIED, joinedAt=now)
        ProjectMember pm = ProjectMember.builder()
                .user(user)
                .project(project)
                .role(ProjectMemberRole.MEMBER) // 기본 포지션
                .position(req.getPosition())
                .techs(req.getTechs())
                .status(ProjectMemberStatus.APPLIED)
                .joinedAt(LocalDateTime.now())
                .build();


        projectMemberRepository.save(pm);

        // 해당 프로젝트의 질문들 가져오기 (순서 포함)
        List<ProjectQuestion> questions =
                projectQuestionRepository.findAllByProjectIdOrderByDisplayOrder(projectId);


        // 요청에서 들어온 답변들을 매핑
        Map<Long, String> answerMap = req.getAnswers().stream()
                .collect(Collectors.toMap(ApplyProjectRequest.AnswerDto::getQuestionId,
                        ApplyProjectRequest.AnswerDto::getAnswer));


        // 3. 질문 답변 저장
        for (ProjectQuestion q : questions) {
            String answerText = answerMap.get(q.getId());

            // 1) 답변 누락 시 예외 발생
            if (answerText == null || answerText.isBlank()) {
                throw new IllegalStateException("질문 [" + q.getQuestionText() + "] 에 대한 답변이 누락되었습니다.");
            }


            // 2) 답변 저장
            ProjectAnswer answer = ProjectAnswer.builder()
                    .member(pm)
                    .question(q)
                    .answerText(answerText)
                    .build();
            projectAnswerRepository.save(answer);


            // 3) 포지션/기술 답변은 ProjectMember에 동기화
            if ("포지션".equals(q.getQuestionText())) {
                pm.updatePosition(answerText);
            }
            if ("기술".equals(q.getQuestionText())) {
                pm.updateTechs(answerText);
            }
        }


        // 입력값을 질문 순서에 맞춰 배열로 (Q1=answer, Q2=position, Q3=techs)
//        List<String> inputs = asList(
//                req.getAnswer(),
//                req.getPosition(),
//                req.getTechs()
//        );
//
//        List<ProjectAnswer> toSave = new ArrayList<>();
//
//        int limit = Math.min(qs.size(), inputs.size());
//
//
//        for (int i = 0; i < limit; i++) {
//            String val = inputs.get(i);
//            if (val == null || val.isBlank()) continue;  // 빈 값은 건너뜀
//
//            ProjectAnswer pa = ProjectAnswer.builder()
//                    .member(pm)
//                    .question(qs.get(i))
//                    .answerText(val)
//                    .build();
//
//            toSave.add(pa);
//        }
//
//        if (!toSave.isEmpty()) {
//            projectAnswerRepository.saveAll(toSave);
//        }

    }

    //전체조회
    @Transactional(readOnly = true)
    public Page<AllProjectResponse> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(p -> AllProjectResponse.builder()
                        .id(p.getId())
                        .slogan(p.getSlogan())
                        .createdAt(p.getCreatedAt())
                        .title(p.getTitle())
                        .imageKey(imageUrlResolver.toPresignedUrl(p.getLogoImageKey()))
                        .build());
//                .map(p -> ProjectResponse.builder()
//                        .id(p.getId())
//                        .title(p.getTitle())
//                        .imageUrl(p.getLogoImageKey()) // 맞는 getter로 사용
//                        .build());
    }

    //아이디로 1개만 조회
    public OneProjectResponse findOneById(Long projectId) {

        Project project = projectRepository.findWithManagerAndMembers(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 없음"));

        List<MemberBrief> members = project.getProjectMembers().stream()
                .filter(pm -> pm.getStatus() == ProjectMemberStatus.APPROVED)
                .map(pm -> MemberBrief.builder()
                        .userId(pm.getUser().getId())
                        .name(pm.getUser().getNickname())
                        .profileKey(imageUrlResolver.toPresignedUrl(pm.getUser().getProfileImageKey()))
                        .build()
                ).toList();

        String leaderProfileImageUrl = project.getManager().getProfileImageKey();
        String presignedLeaderProfileImageUrl = imageUrlResolver.toPresignedUrl(leaderProfileImageUrl);

        return OneProjectResponse.builder()
                .id(projectId)
                .content(project.getDescription())
                .leaderImage(presignedLeaderProfileImageUrl)
                .title(project.getTitle())
                .memberBriefs(members)
                .techs(project.getTechs())
                .slogan(project.getSlogan())
                .build();


    }
    //신청자 목록 조회
    @Transactional(readOnly = true)
    public List<ProjectApplicationResponse> getApplication(Long userId, Long projectId) {
        // 1) 팀장 검증
        Long managerId = projectRepository.findManagerId(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 없음"));
        if (!managerId.equals(userId)) {
            throw new IllegalStateException("해당 프로젝트의 팀장이 아닙니다.");
        }

        // 2) 신청자 + 유저정보 로딩 (N+1 방지)
        List<ProjectMember> applicants =
                projectMemberRepository.findAllApplicantsWithUser(projectId);

        if (applicants.isEmpty()) return List.of();

        // 3) 신청자 PM id 목록
        List<Long> memberIds = applicants.stream()
                .map(ProjectMember::getId)
                .toList();

        // 4) 모든 답변 한 번에 로딩 (질문 displayOrder로 정렬)
        List<ProjectAnswer> allAnswers =
                projectAnswerRepository.findAllByMemberIdInOrderByMemberAndQuestion(memberIds);

        // 5) 신청자별로 답변 그룹핑 → DTO 변환
        Map<Long, List<ProjectApplicationResponse.AnswerResponseDTO>> answersByMember =
                allAnswers.stream()
                        .collect(Collectors.groupingBy(
                                pa -> pa.getMember().getId(),
                                Collectors.mapping(pa -> ProjectApplicationResponse.AnswerResponseDTO.builder()
                                                .questionId(pa.getQuestion().getId())
                                                .questions(pa.getQuestion().getQuestionText())
                                                .answer(pa.getAnswerText())
                                                .build(),
                                        Collectors.toList()
                                )
                        ));

        // 6) 최종 응답
        return applicants.stream()
                .map(pm -> ProjectApplicationResponse.builder()
                        .id(pm.getId())
                        .projectId(projectId)
                        .userId(pm.getUser().getId())
                        .nickName(pm.getUser().getNickname())
                        .profileImage(imageUrlResolver.toPresignedUrl(pm.getUser().getProfileImageKey()))
                        .position(pm.getPosition())
                        .techs(pm.getTechs())
                        .answers(answersByMember.getOrDefault(pm.getId(), List.of()))
                        .build())
                .toList();
    }

    // presign 변환 메서드
    private String resolveProfileImageUrl(User user) {
        if (user.getProfileImageKey() == null) return null;
        return r2StorageService.presignGet(user.getProfileImageKey(), 3600).toString();
    }

}