package com.cdy.cdy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyCategoryResponse {
    private Long id;
    private String name;
}