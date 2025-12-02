package com.cdy.cdy.service;

import com.cdy.cdy.dto.response.HomeBannerResponseDto;
import com.cdy.cdy.dto.response.HomePartnerResponseDto;
import com.cdy.cdy.entity.Banner;
import com.cdy.cdy.entity.Partner;
import com.cdy.cdy.repository.BannerRepository;
import com.cdy.cdy.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HomeService {

    private final BannerRepository bannerRepository;
    private final ImageUrlResolver imageUrlResolver;
    private final PartnerRepository partnerRepository;

    public List<HomeBannerResponseDto> findBanners() {

        List<Banner> all = bannerRepository.findAll();

        return all.stream().map(et ->
                HomeBannerResponseDto.builder()
                        .id(et.getId())
                        .link(et.getLink())
                        .imageUrl(imageUrlResolver.toPresignedUrl(et.getImageKey()))
                        .build()
        ).toList();

    }

    public List<HomePartnerResponseDto> findPartners() {

        List<Partner> all = partnerRepository.findAll();

        return all.stream().map(et ->
                HomePartnerResponseDto.builder()
                        .id(et.getId())
                        .link(et.getLink())
                        .imageUrl(imageUrlResolver.toPresignedUrl(et.getImageKey()))
                        .build()

        ).toList();




    }
}
