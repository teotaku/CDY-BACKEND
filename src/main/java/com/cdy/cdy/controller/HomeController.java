package com.cdy.cdy.controller;

import com.cdy.cdy.dto.response.HomeBannerResponseDto;
import com.cdy.cdy.dto.response.HomePartnerResponseDto;
import com.cdy.cdy.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;


    @Operation(summary = "배너 조회", description = "홈화면 배너 조회")
    @GetMapping("/findBanners")
    public ResponseEntity<List<HomeBannerResponseDto>> findBanners() {

        List<HomeBannerResponseDto> dto = homeService.findBanners();

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "파트너 조회", description = "홈화면 파트너 조회")
    @GetMapping("/findPartners")
    public ResponseEntity<List<HomePartnerResponseDto>> findPartners() {

        List<HomePartnerResponseDto> dto = homeService.findPartners();

        return ResponseEntity.ok(dto);

    }
}
