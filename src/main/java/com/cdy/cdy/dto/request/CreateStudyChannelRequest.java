package com.cdy.cdy.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CreateStudyChannelRequest {

    @Schema(description = "스터디 이미지 목록")
   @Builder.Default
     private List<CreateStudyImageDto> images = new ArrayList<>();
    @Schema(description = "스터디 내용", example = "이번 주 공부내용!")
    @NotBlank
    private String content;
}
