package com.cdy.cdy.dto.response.project;


import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CompleteProject {

    Long id;
    String logoImageURL;

}
