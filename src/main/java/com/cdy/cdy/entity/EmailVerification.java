package com.cdy.cdy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "email_verifications")
public class EmailVerification extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    // ↑ @ManyToOne 기본 fetch=EAGER지만 실무에선 LAZY로 강제 전환(불필요 조인/쿼리 방지)
    @JoinColumn(name = "user_id", nullable = false) // ↑ FK 컬럼명 고정
    private User user; // ← 여러 인증 레코드(N)가 한 유저(1)를 가리키므로 N:1(=ManyToOne)

    @Column(length = 10, nullable = false)
    private String code; // 인증번호

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt; // 만료시각

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt; // 실제 검증 완료시각(전엔 null)

    public static EmailVerification issue(User user, String code, LocalDateTime expiresAt) {
        Objects.requireNonNull(user);       // (1) 널 가드: 필수 인자 체크
        Objects.requireNonNull(code);       // (2)
        Objects.requireNonNull(expiresAt);  // (3)

        var v = new EmailVerification();    // (4) JPA 엔티티 인스턴스 생성
        v.user = user;                      // (5) 소유자 설정
        v.code = code;                      // (6) 코드 세팅(이미 서비스에서 생성해 전달)
        v.expiresAt = expiresAt;            // (7) 만료 시각 세팅
        // v.verifiedAt = null               // (8) 아직 검증 전이므로 null (기본)
        return v;                           // (9) 완성본 반환(= 발급 객체)
    }

    public void verify(LocalDateTime when) {
        this.verifiedAt = (when == null ? LocalDateTime.now() : when); }
}


