package com.cdy.cdy.service;


import com.cdy.cdy.dto.request.CreateProjectRequest;
import com.cdy.cdy.dto.response.ProjectResponse;
import com.cdy.cdy.entity.*;
import com.cdy.cdy.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectService {


    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectQuestionRepository projectQuestionRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectTechRepository projectTechRepository;

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

            // 6) 응답 반환
            return ProjectResponse.of(
                    project,
                    leader.getId(),
                    req.getPositions(),
                    req.getTechs(),
                    req.getQuestions()
            );
        }
}