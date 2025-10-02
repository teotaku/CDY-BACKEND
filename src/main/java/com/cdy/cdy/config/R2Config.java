package com.cdy.cdy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import java.net.URI;

@Configuration
public class R2Config {

    /** R2에 실제 요청(업로드/다운로드/삭제)을 날리는 클라이언트 */
    @Bean
    public S3Client s3Client(StorageProps p) {
        return S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(p.getAccessKey(), p.getSecretKey())))
            .region(Region.of(p.getRegion()))                    // R2는 'auto' 같은 더미 OK
            .endpointOverride(URI.create(p.getEndpoint()))       // https://<ACCOUNT>.r2.cloudflarestorage.com
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(true)                    // R2는 path-style 필요
                .build())
            .httpClientBuilder(UrlConnectionHttpClient.builder())
            .build();
    }

    /** 프리사인 URL 발급용 */
    @Bean
    public S3Presigner s3Presigner(StorageProps p) {
        return S3Presigner.builder()
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(p.getAccessKey(), p.getSecretKey())))
            .region(Region.of(p.getRegion()))
            .endpointOverride(URI.create(p.getEndpoint()))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)   // ✅ 이 줄 추가
                        .build())
            .build();
    }
}
