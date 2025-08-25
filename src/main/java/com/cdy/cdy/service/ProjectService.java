package com.cdy.cdy.service;


import com.cdy.cdy.dto.request.CreateProjectRequest;
import com.cdy.cdy.dto.response.ProjectResponse;
import com.cdy.cdy.entity.*;
import com.cdy.cdy.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectService {


    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectQuestionRepository projectQuestionRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectTechRepository projectTechRepository;




        //프로젝트 생성
        public ProjectResponse createProject(Long leaderUserId,
                CreateProjectRequest req
                                             ) {


            // 1) 유저 조회
            User leader = userRepository.findById(leaderUserId)
                    .orElseThrow(() -> new EntityNotFoundException("user not found"));

            // 2) 프로젝트 생성
            Project project = Project.from(req, leader);
            projectRepository.save(project);

            // 3) 리더 등록
            ProjectMember leaderMember = ProjectMember.builder()
                    .project(project)
                    .user(leader)
                    .role(ProjectMemberRole.LEADER)
                    .build();
            projectMemberRepository.save(leaderMember);

            // 4) 기술 저장 (문자열)
            if (req.getTechs() != null) {
                for (String tech : req.getTechs()) {
                    projectTechRepository.save(ProjectTech.builder()
                            .project(project)
                            .techName(tech)
                            .build());
                }
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

            // 6) 응답 반환
            return ProjectResponse.of(
                    project,
                    leader.getId(),
                    req.getPositions(),
                    req.getTechs(),
                    req.getQuestions(),
                    memberCount,
                    phone
            );
        }

        //진행중 프로젝트
    public ProjectResponse getProgressingProject(Long userId) {
        ProjectMember pm = projectMemberRepository
                .findTopByUserIdAndStatusOrderByJoinedAtDesc(userId, ProjectMemberStatus.APPROVED)
                .orElseThrow(() -> new EntityNotFoundException("신청중인 프로젝트가 없습니다."));

        Project p = pm.getProject();
        long memberCount = projectMemberRepository.countByProjectId(p.getId());

        return ProjectResponse.of(
                p,
                p.getManager().getId(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                (int) memberCount,
                p.getContact()
        );

    }
        //신청중 프로젝트
    public ProjectResponse getApplyProject(Long userId) {

        ProjectMember pm = projectMemberRepository
                .findTopByUserIdAndStatusOrderByJoinedAtDesc(userId, ProjectMemberStatus.APPLIED)
                .orElseThrow(() -> new EntityNotFoundException("진행중인 프로젝트가 없습니다."));

        Project p = pm.getProject();
        long memberCount = projectMemberRepository.countByProjectId(p.getId());

        return ProjectResponse.of(
                p,
                p.getManager().getId(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                (int) memberCount,
                p.getContact()
        );



    }

    public void applyToProject(Long userId, Long projectId, ProjectMemberRole role) {

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
                .role(role != null ? role : ProjectMemberRole.MEMBER) // 기본 포지션
                .status(ProjectMemberStatus.APPLIED)
                .joinedAt(LocalDateTime.now()) // 또는 @PrePersist 로 자동 세팅
                .build();

        projectMemberRepository.save(pm);
    }
}