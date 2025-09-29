package com.cdy.cdy.service;

import com.cdy.cdy.dto.response.ProjectCompleteResponse;
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

    //질문 목록 가져오기
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
    @Transactional
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
    @Transactional
    public ProjectCompleteResponse  complete(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 없음"));

             ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                        .orElseThrow(()-> new EntityNotFoundException("프로젝트에 참가하고있지않습니다."));

        if (projectMember.isCompleted()) {
            int total = projectMemberRepository.findApprovedMembersWithUserByProjectId(projectId).size();
            long completed = projectMemberRepository.findApprovedMembersWithUserByProjectId(projectId).stream()
                    .filter(ProjectMember::isCompleted).count();
            double rate = total == 0 ? 0 : (completed * 100.0) / total;

            return ProjectCompleteResponse.builder()
                    .success(true)
                    .status("WAITING")
                    .message("이미 완료 처리된 사용자입니다.")
                    .data(ProjectCompleteResponse.Data.builder()
                            .userRole(projectMember.getRole().name())
                            .completedMembers((int) completed)
                            .totalMembers((int) total)
                            .completionRate(rate)
                            .build())
                    .build();
        }


        if (projectMember.getStatus() != ProjectMemberStatus.APPROVED) {
            throw new IllegalStateException("승인된 멤버만 프로젝트 완료 처리할 수 있습니다.");
        }
        // 팀장이라면 → 무조건 "다른 멤버가 완료했는지" 확인
        if (project.getManager().getId().equals(userId)) {
            boolean othersCompleted = projectMemberRepository
                    .findApprovedMembersWithUserByProjectId(projectId).stream()
                    .filter(pm -> !pm.getUser().getId().equals(userId)) // 팀장 제외
                    .allMatch(ProjectMember::isCompleted);

            if (!othersCompleted) {
                throw new IllegalStateException("팀장은 모든 멤버가 완료한 이후에만 완료할 수 있습니다.");
            }
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

        int total = projectMemberRepository.findApprovedMembersWithUserByProjectId(projectId).size();
        long completed = projectMemberRepository.findApprovedMembersWithUserByProjectId(projectId).stream()
                .filter(ProjectMember::isCompleted).count();
        double rate = total == 0 ? 0 : (completed * 100.0) / total;

        return ProjectCompleteResponse.builder()
                .success(true)
                .status(allCompleted ? "COMPLETED" : "WAITING")
                .message(allCompleted ? "프로젝트 최종 완료" : "팀장의 완료를 기다리는 중")
                .data(ProjectCompleteResponse.Data.builder()
                        .userRole(projectMember.getRole().name())
                        .completedMembers((int) completed)
                        .totalMembers(total)
                        .completionRate(rate)
                        .build())
                .build();

    }
}