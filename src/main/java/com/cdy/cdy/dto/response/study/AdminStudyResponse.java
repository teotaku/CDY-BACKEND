package com.cdy.cdy.dto.response.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminStudyResponse {

        private Long id;
        private String content;
        private LocalDateTime createdAt;
//        private String userName;

}
