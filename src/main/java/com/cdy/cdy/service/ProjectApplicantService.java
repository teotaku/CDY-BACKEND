package com.cdy.cdy.service;

import com.cdy.cdy.dto.response.project.ProjectQuestionResponse;
import com.cdy.cdy.entity.project.Project;
import com.cdy.cdy.entity.project.ProjectMember;
import com.cdy.cdy.entity.project.ProjectMemberStatus;
import com.cdy.cdy.entity.project.ProjectQuestion;
import com.cdy.cdy.repository.ProjectMemberRepository;
import com.cdy.cdy.repository.ProjectQuestionRepository;
import com.cdy.cdy.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectApplicantService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectQuestionRepository projectQuestionRepository;
    private final ProjectRepository projectRepository;

    //신청승인
    @Transactional
    public void approve(Long projectId, Long applicantUserId, Long currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 없음"));


        ProjectMember pm = projectMemberRepository
                .findByProjectIdAndUserId(projectId, applicantUserId)
                .orElseThrow(() -> new EntityNotFoundException("신청 내역 없음"));

        // 팀장 검증
        if (!project.getManager().getId().equals(currentUserId)) {
            throw new AccessDeniedException("팀장만 승인할 수 있습니다.");
        }

        pm.approve(); // ← 도메인 메서드 호출
    }

    /**
     * 신청 거절
     */
    @Transactional
    public void reject(Long projectId, Long applicantUserId, Long currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 없음"));

        // 팀장 검증
        if (!project.getManager().getId().equals(currentUserId)) {
            throw new AccessDeniedException("팀장만 거절할 수 있습니다.");
        }

        ProjectMember pm = projectMemberRepository
                .findByProjectIdAndUserId(projectId, applicantUserId)
                .orElseThrow(() -> new EntityNotFoundException("신청 내역 없음"));

        pm.reject(); // ← 도메인 메서드 호출
    }

    public List<ProjectQuestionResponse> getQuestions(Long projectId) {

        List<ProjectQuestion> questions =
                projectQuestionRepository.findAllByProjectIdOrderByDisplayOrder(projectId);

        List<ProjectQuestionResponse> list = questions.stream().map(qus -> ProjectQuestionResponse.builder()
                        .id(qus.getId())
                        .projectId(qus.getProject().getId())
                        .content(qus.getQuestionText())
                        .build())
                .toList();

        return list;
    }
    //신청취소
    public void cancel(Long projectId, Long userId) {

        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(()-> new EntityNotFoundException("프로젝트에 참가하고있지않습니다."));

        if (projectMember.getStatus() != ProjectMemberStatus.APPROVED
                && projectMember.getStatus() != ProjectMemberStatus.APPLIED) {
            throw new IllegalStateException("신청중이거나 참여중일 때만 취소할 수 있습니다.");
        }


        projectMember.cancel();
    }
    //프로젝트 완료
    public void complete(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 없음"));

             ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                        .orElseThrow(()-> new EntityNotFoundException("프로젝트에 참가하고있지않습니다."));



        if (projectMember.getStatus() != ProjectMemberStatus.APPROVED) {
            throw new IllegalStateException("승인된 멤버만 프로젝트 완료 처리할 수 있습니다.");
        }


        projectMember.complete();

        // 모든 멤버가 COMPLETE 상태인지 체크
        boolean allCompleted = projectMemberRepository
                .findApprovedMembersWithUserByProjectId(projectId)
                .stream()
                .allMatch(pm -> pm.isCompleted());

        // 팀장이 마지막에 완료 누르면 프로젝트 자체 완료
        if (allCompleted && project.getManager().getId().equals(userId)) {
            project.complete();
        }

    }
}