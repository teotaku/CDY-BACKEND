package com.cdy.cdy.dto.response.project;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SingleProjectResponse {

    private Long id;

    private String imageUrl;

    private String content;
}
