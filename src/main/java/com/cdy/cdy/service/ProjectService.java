package com.cdy.cdy.service;


import com.cdy.cdy.dto.request.CreateProjectQuestionRequest;
import com.cdy.cdy.dto.request.CreateProjectRequest;
import com.cdy.cdy.dto.response.MemberBrief;
import com.cdy.cdy.dto.response.ProjectResponse;
import com.cdy.cdy.entity.*;
import com.cdy.cdy.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

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
                .slogan(req.getSlogan())
                .status(ProjectStatus.IN_PROGRESS)
                .logoImageUrl(req.getImageKey()) // null 가능
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

        // 5) 질문 저장
        if (req.getQuestions() != null) {
            for (String q : req.getQuestions()) {
                projectQuestionRepository.save(ProjectQuestion.builder()
                        .project(project)
                        .content(q)
                        .build());
            }
        }

        long memberCount = projectMemberRepository.countByProjectId(project.getId());
        String phone = leader.getPhoneNumber(); // 또는 req.getContact()
    }

    //진행중 프로젝트
    public ProjectResponse getProgressingProject(Long userId) {

        Project project = projectRepository.findProgressingProjectByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("진행중인 프로젝트가 없습니다."));

//        ProjectMember pm = projectMemberRepository
//                .findTopByUserIdAndStatusOrderByJoinedAtDesc(userId, ProjectMemberStatus.APPROVED)
//                .orElseThrow(() -> new EntityNotFoundException("신청중인 프로젝트가 없습니다."));


        long memberCount = projectMemberRepository.countByProjectId(project.getId());

        List<ProjectMember> members = projectMemberRepository
                .findApprovedMembersWithUserByProjectId(project.getId());



        List<MemberBrief> memberBriefs = members.stream()
                .map(pm -> MemberBrief.builder()
                        .userId(pm.getUser().getId())
                        .name(pm.getUser().getNickname())
                        .profileUrl(pm.getUser().getProfileImageUrl())
                        .build()).toList();


        return ProjectResponse.builder()
                .title(project.getTitle())
                .memberCount(memberCount)
                .techs(project.getTechs())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .imageUrl(project.getLogoImageUrl())
                .kakaoLink(project.getKakaoLink())
                .memberBriefs(memberBriefs)
                .slogan(project.getSlogan())
                .build();

    }

    //신청중 프로젝트
    public ProjectResponse getApplyProject(Long userId) {

        ProjectMember pm = projectMemberRepository
                .findTopByUserIdAndStatusOrderByJoinedAtDesc(userId, ProjectMemberStatus.APPLIED)
                .orElseThrow(() -> new EntityNotFoundException("신청중인 프로젝트가 없습니다."));

        Project p = pm.getProject();
        long memberCount = projectMemberRepository.countByProjectId(p.getId());


        List<ProjectMember> members = projectMemberRepository
                .findApprovedMembersWithUserByProjectId(p.getId());

        List<MemberBrief> memberBriefs = members.stream()
                .map(pmb -> MemberBrief.builder()
                        .userId(pmb.getUser().getId())
                        .name(pmb.getUser().getNickname())
                        .profileUrl(pmb.getUser().getProfileImageUrl())
                        .build()).toList();




        return ProjectResponse.builder()
                .title(p.getTitle())
                .memberCount(memberCount)
                .techs(p.getTechs())
                .description(p.getDescription())
                .createdAt(p.getCreatedAt())
                .imageUrl(p.getLogoImageUrl())
                .kakaoLink(p.getKakaoLink())
                .memberBriefs(memberBriefs)
                .slogan(p.getSlogan())
                .build();
    }


    //프로젝트 신청
    public void applyToProject(Long userId, Long projectId,
                               CreateProjectQuestionRequest req) {

        // 1) 참조 로딩 (존재 검증)
        User user = userRepository.getReferenceById(userId); // 존재 보장 시 getReferenceById OK
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트가 없습니다."));

        // 2) 자기 프로젝트에 신청 금지
        if (project.getManager().getId().equals(userId)) {
            throw new IllegalStateException("본인이 관리자인 프로젝트에는 신청할 수 없습니다.");
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
                .status(ProjectMemberStatus.APPLIED)
                .joinedAt(LocalDateTime.now()) // 또는 @PrePersist 로 자동 세팅
                .build();


        projectMemberRepository.save(pm);

        List<ProjectQuestion> qs =
                projectQuestionRepository.findAllByProject_IdOrderByIdAsc(projectId);


        // 입력값을 질문 순서에 맞춰 배열로 (Q1=answer, Q2=position, Q3=techs)
        List<String> inputs = asList(
                req.getAnswer(),
                req.getPosition(),
                req.getTechs()
        );

        List<ProjectAnswer> toSave = new ArrayList<>();

        int limit = Math.min(qs.size(), inputs.size());


        for (int i = 0; i < limit; i++) {
            String val = inputs.get(i);
            if (val == null || val.isBlank()) continue;  // 빈 값은 건너뜀

            ProjectAnswer pa = ProjectAnswer.builder()
                    .member(pm)
                    .question(qs.get(i))
                    .answerText(val)
                    .build();

            toSave.add(pa);
        }

        if (!toSave.isEmpty()) {
            projectAnswerRepository.saveAll(toSave);
        }

    }
    //전체조회
    @Transactional(readOnly = true)
    public Page<ProjectResponse> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(p -> ProjectResponse.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .imageUrl(p.getLogoImageUrl()) // 맞는 getter로 사용
                        .build());
    }

    //아이디로 1개만 조회
    public ProjectResponse findOneById(Long projectId) {

        Project project = projectRepository.findWithManagerAndMembers(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 없음"));

        List<MemberBrief> members = project.getProjectMembers().stream()
                .filter(pm -> pm.getStatus() == ProjectMemberStatus.APPROVED)
                .map(pm -> MemberBrief.builder()
                        .userId(pm.getUser().getId())
                        .name(pm.getUser().getNickname())
                        .profileUrl(pm.getUser().getProfileImageUrl())
                        .build()
                ).toList();

        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .slogan(project.getSlogan())
                .imageUrl(project.getLogoImageUrl())
                .leaderImage(project.getManager().getProfileImageUrl())
                .memberBriefs(members)
                .memberCount(members.size())
                .build();

    }


}