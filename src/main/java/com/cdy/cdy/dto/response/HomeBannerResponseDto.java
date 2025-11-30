package com.cdy.cdy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class HomeBannerResponseDto {

        private Long id;
    private String link;
    private String imageUrl;

}
