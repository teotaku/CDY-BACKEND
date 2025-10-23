package com.cdy.cdy.dto.response.project;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AdminProjectResponse {

    private Long id;

    private String ProjectImageUrl;


}
