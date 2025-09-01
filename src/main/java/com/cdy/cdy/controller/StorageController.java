package com.cdy.cdy.controller;

import com.cdy.cdy.dto.request.PresignRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.PresignResponse;
import com.cdy.cdy.service.R2StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

    private final R2StorageService storageService;

    @PostMapping("/presign")
    public PresignResponse presign(@RequestBody PresignRequest req,
                                   @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getId();                                          // [A] 로그인 유저 id 꺼냄

        String key = storageService.buildKey(                       // [B] 업로드 대상 "key" 생성
                req.getOriginalFilename()
        ); // 예: "studies/42/2025/08/31/b8f9...c1.jpg"

        URL uploadUrl = storageService.presignPut(                            // [C] 그 key로 PUT URL 만들기
                key, req.getContentType() // ★ 나중에 PUT할 때 이 Content-Type과 "완전히" 같아야 함
        );

        return new PresignResponse(key, uploadUrl.toString(), 600);          // [D] 클라로 전달
    }
}