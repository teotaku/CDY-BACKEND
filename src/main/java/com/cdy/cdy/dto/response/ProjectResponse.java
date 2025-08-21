package com.cdy.cdy.dto.response;

import com.cdy.cdy.entity.Project;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private String imageUrl;


    private int memberCount;     // 참여 인원 수
    private String contact;      // 연락처

    private Long leaderId;
    private List<String> positions;
    private List<String> techs;
    private List<String> questions;

    public static ProjectResponse of(Project p,
                                     Long leaderId,
                                     List<String> positions,
                                     List<String> techs,
                                     List<String> questions, int memberCount,String phoneNumber) {
        return ProjectResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .imageUrl(p.getLogoImageUrl()) // 지금은 null이어도 OK
                .leaderId(leaderId)
                .positions(positions)
                .techs(techs)
                .questions(questions)
                .contact(phoneNumber)
                .memberCount(memberCount)
                .build();

    }
}
