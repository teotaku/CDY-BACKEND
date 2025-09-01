package com.cdy.cdy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.*;

/**
 * application.yml 의 storage: 값을 바인딩하는 단순 설정 객체.
 * - bucket: 버킷명 (예: cdy-img)
 * - accessKey: R2 Access Key ID
 * - secretKey: R2 Secret Access Key
 * - endpoint: https://<ACCOUNT_ID>.r2.cloudflarestorage.com   (← /버킷명 붙이지 말기)
 * - region: R2는 의미 없어서 'auto' 같은 더미값
 * - presignSeconds: 프리사인 URL 유효시간(초)
 */
@Getter @Setter
@ToString(exclude = "secretKey")                  // 로그에 비밀키 안 찍히게
@Component                                        // 스프링 빈 등록
@ConfigurationProperties(prefix = "storage")      // storage.* 매핑
public class StorageProps {
    private String provider;      // 식별용(선택)
    private String bucket;        // 버킷명
    private String accessKey;     // Access Key ID
    private String secretKey;     // Secret Access Key
    private String endpoint;      // R2 S3 호환 엔드포인트(도메인만)
    private String region;        // 'auto' 등 더미
    private int presignSeconds;   // 프리사인 URL 유효시간(초)
}
