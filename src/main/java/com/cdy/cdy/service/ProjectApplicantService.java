package com.cdy.cdy.service;

import com.cdy.cdy.dto.response.ApplicantCardResponse;
import com.cdy.cdy.entity.ProjectAnswer;
import com.cdy.cdy.entity.ProjectMember;
import com.cdy.cdy.entity.ProjectMemberStatus;
import com.cdy.cdy.repository.ProjectAnswerRepository;
import com.cdy.cdy.repository.ProjectMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectApplicantService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectAnswerRepository projectAnswerRepository;


    /** 팀장이 보는 신청자 카드 목록 */
    public List<ApplicantCardResponse> getApplicants(Long projectId) {
        // 1) 신청자(APPLIED) + User 한 방 조회
        List<ProjectMember> members = projectMemberRepository.findApplicants(projectId);

        if (members.isEmpty()) return List.of();

        // 2) memberIds 뽑기
        List<Long> memberIds = members.stream()
                .map(ProjectMember::getId)
                .toList();

        // 3) 모든 답변 한 방 조회 (질문까지 필요하면 join fetch 버전 사용)
        List<ProjectAnswer> answers =
                projectAnswerRepository.findAllByMemberIdInOrderByQuestionId(memberIds);

        // 4) memberId -> 답변목록 매핑
        Map<Long, List<ProjectAnswer>> byMember = answers.stream()
                .collect(Collectors.groupingBy(pa -> pa.getMember().getId()));

        // 5) 신청자 카드로 빌드
        return members.stream().map(pm -> {
            List<ProjectAnswer> myAnswers = byMember.getOrDefault(pm.getId(), List.of());

            List<ApplicantCardResponse.AnswerItem> answerItems = myAnswers.stream()
                    .map(pa -> new ApplicantCardResponse.AnswerItem(
                            pa.getQuestion().getId(),
                            pa.getQuestion().getQuestionText(), // 질문 텍스트가 필요 없으면 제거
                            pa.getAnswerText()
                    ))
                    .toList();

            return ApplicantCardResponse.builder()
                    .userId(pm.getUser().getId())
                    .nickname(pm.getUser().getNickname())
                    .profileImageUrl(pm.getUser().getProfileImageUrl())
                    .answers(answerItems) // Q1, Q2, Q3 …
                    .build();
        }).toList();
    }


    @Transactional
    public void approve(Long projectId, Long applicantUserId) {
        ProjectMember pm = projectMemberRepository
                .findByProjectIdAndUserId(projectId, applicantUserId)
                .orElseThrow(() -> new EntityNotFoundException("신청 내역 없음"));

        pm.approve(); // ← 도메인 메서드 호출
    }

    /** 신청 거절 */
    @Transactional
    public void reject(Long projectId, Long applicantUserId) {
        ProjectMember pm = projectMemberRepository
                .findByProjectIdAndUserId(projectId, applicantUserId)
                .orElseThrow(() -> new EntityNotFoundException("신청 내역 없음"));

        pm.reject(); // ← 도메인 메서드 호출
    }
}
