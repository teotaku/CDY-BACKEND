package com.cdy.cdy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomePartnerResponseDto {

    private Long id;

    private String link;

    private String imageUrl;

}
