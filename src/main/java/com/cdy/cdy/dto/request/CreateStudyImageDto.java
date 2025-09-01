package com.cdy.cdy.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStudyImageDto {
    private String key;        // presign 단계에서 받은 그 key
    private Integer sortOrder; // 0,1,2...
//    private String alt;        // 선택
    // getter/setter...
}