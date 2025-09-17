package com.cdy.cdy.service;

import com.cdy.cdy.entity.ProjectAnswer;
import com.cdy.cdy.entity.ProjectMember;
import com.cdy.cdy.repository.ProjectAnswerRepository;
import com.cdy.cdy.repository.ProjectMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectApplicantService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectAnswerRepository projectAnswerRepository;




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
