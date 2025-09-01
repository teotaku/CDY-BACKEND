package com.cdy.cdy.dto.request;

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


   @Builder.Default
     private List<CreateStudyImageDto> images = new ArrayList<>();

    @NotBlank
    private String content;
}
