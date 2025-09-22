package com.cdy.cdy.dto.request;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudyImageDto {

    private Long id;          // 기존 이미지면 PK, 신규면 null
    private String key;       // 새로 업로드한 R2/S3 key
    private Integer sortOrder; // 정렬 순서(옵션)
}

