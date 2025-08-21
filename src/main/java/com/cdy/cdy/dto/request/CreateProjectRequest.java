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

    @NotBlank
    @Size(max = 255)
    private String title;          // 프로젝트명

    @NotBlank
    private String description;    // 프로젝트 설명

    @NotNull
    @Positive
    private Integer capacity;      // 참여 인원

    private List<String> positions; // 포지션 ["FE", "BE"]

    private List<String> techs;     // 기술 ["Spring", "React"]

    private List<String> questions; // 질문 ["자기소개", "가능 요일"]
}
