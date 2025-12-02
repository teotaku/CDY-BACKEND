package com.cdy.cdy.service;

import com.cdy.cdy.config.StorageProps;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Client s3;            // R2Config에서 만든 S3 클라이언트 빈
    private final S3Presigner presigner;  // 프리사인 URL 발급용
    private final StorageProps props;     // storage.* 프로퍼티 바인딩 객체

    /** 프로필 이미지 업로드 후 '접근 가능한 URL'을 반환 */
    @Transactional
    public String uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        // 1) 파일 확장자 추출(없으면 빈 문자열)
        String original = file.getOriginalFilename();
        String ext = (original != null && original.lastIndexOf('.') != -1)
                ? original.substring(original.lastIndexOf('.')) : "";

        // 2) 객체 키 생성(경로 + 시간 + 랜덤) → 충돌 피하고, 유저별로 디렉토리 구분
        String key = "profile/" + userId + "/" + System.currentTimeMillis() + "-" + UUID.randomUUID() + ext;

        // 3) PutObjectRequest 구성: 매 '요청'마다 bucket과 key를 지정해야 함
        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(props.getBucket())                 // ← 버킷명은 설정에서 읽어옴
                .key(key)                                   // ← 파일이 저장될 S3(Object Storage) 경로
                .contentType(file.getContentType())         // ← 브라우저가 열 수 있도록 MIME 타입 설정
                .cacheControl("public, max-age=31536000")   // ← (선택) 캐시 헤더
                .build();

        // 4) 실제 업로드(바이트로 변환하여 전송)
        s3.putObject(put, RequestBody.fromBytes(file.getBytes()));

        // 5) URL 반환 방식 선택
        // 5-1) 버킷이 Public이면 '엔드포인트/버킷/키' 형태의 퍼블릭 URL로 접근 가능
        String publicUrl = props.getEndpoint() + "/" + props.getBucket() + "/" + key;

        // 5-2) Private이면 프리사인 URL을 만들어 반환(유효시간은 props.presignSeconds)
        // URL signedUrl = presignGet(key);

        return publicUrl; // 환경에 맞게 위 둘 중 하나 선택
    }

    /** (선택) Private 버킷용: 프리사인 GET URL 생성 */
    public URL presignGet(String key) {
        GetObjectRequest get = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build();

        GetObjectPresignRequest req = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(props.getPresignSeconds()))
                .getObjectRequest(get)
                .build();

        return presigner.presignGetObject(req).url();
    }

    /** (선택) 객체 삭제 */
    @Transactional
    public void delete(String key) {
        s3.deleteObject(b -> b.bucket(props.getBucket()).key(key));
    }
}
