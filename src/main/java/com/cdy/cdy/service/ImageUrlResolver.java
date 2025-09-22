package com.cdy.cdy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageUrlResolver {

    private final R2StorageService r2StorageService;

    /**
     * key를 presign된 URL로 변환
     * @param key DB에 저장된 key (null 가능)
     * @return presign URL (없으면 null)
     */
    public String toPresignedUrl(String key) {
        if (key == null) return null;
        return r2StorageService.presignGet(key, 3600).toString();
    }
}
