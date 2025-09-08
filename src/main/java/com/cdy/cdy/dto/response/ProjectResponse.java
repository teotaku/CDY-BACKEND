package com.cdy.cdy.dto.response;

import com.cdy.cdy.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private String imageUrl;

    private List<MemberBrief> memberBriefs;

    private long memberCount;     // 참여 인원 수
    private String kakakoLink;      // 연락처

    private Long leaderId;
    private List<String> positions;
    private List<String> techs;
    private List<String> questions;


    }
