package com.cdy.cdy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectRequest {

    private String title;          // 프로젝트명

    private String description;    // 프로젝트 설명

    private String imageKey;

    @Positive
    private Integer capacity;      // 참여 인원

    private List<String> positions; // 포지션 ["FE", "BE"]

    private List<String> techs;     // 기술 ["Spring", "React"]

    private List<String> questions; // 질문 ["자기소개", "가능 요일"]

    private String kakaoLink;
}
