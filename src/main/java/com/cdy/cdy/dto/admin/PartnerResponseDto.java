package com.cdy.cdy.dto.admin;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerResponseDto {

    private Long id;
    private String name;
    private String imageUrl;

}
