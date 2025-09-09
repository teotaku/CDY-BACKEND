package com.cdy.cdy.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudyChannelRequest {
    private String title;
    private String content;   // 필요 필드만(네 엔티티 update 시그니처와 맞춰)
    private List<UpdateStudyImageDto> images;
}