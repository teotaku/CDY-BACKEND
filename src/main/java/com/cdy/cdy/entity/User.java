package com.cdy.cdy.entity;



import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
// ↑ @NoArgsConstructor: JPA 프록시/리플렉션용 기본 생성자 필요
//   access = PROTECTED: 외부에서 new User() 금지(생성 루트 통일 목적)
@Entity
@Table(name = "users") // ← 테이블명 지정(관례와 다르면 명시)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // ↑ PK, DB auto_increment 사용 (MySQL 등)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(length = 50,  unique = true)
    // ↑ unique=true: 컬럼 단일 유니크 제약(중복 닉네임 방지)
    private String nickname;

    @Column(length = 255, nullable = false, unique = true)
    // ↑ 이메일도 단일 유니크 제약(중복 가입 방지)
    private String email;

    private String phoneNumber;

    @Column(name = "avatar_key")
    private String profileImageKey;

    @Column(name = "password_hash", length = 255, nullable = false)
    // ↑ 비밀번호 해시(평문 금지). 컬럼명 스네이크로 고정
    private String passwordHash;

    @Column(name = "off_line")
    private Long offLine;

    @Enumerated(EnumType.STRING) // ↑ Enum을 문자열로 저장(숫자 ordinal은 위험)
    @Column(length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserCategory category;

    private Boolean deleted;


   ;

    // --- 생성 루트(Setter 대신) ---
    public static User create(String nickname, String email, String passwordHash, UserRole role) {
//        Objects.requireNonNull(nickname);
//        Objects.requireNonNull(email);
//        Objects.requireNonNull(passwordHash);
//        var u = new User();
//        u.nickname = nickname;
//        u.email = email;
//        u.passwordHash = passwordHash;
//        u.role = (role == null ? UserRole.USER : role);
//        return u;

        return User.builder()
                .nickname(nickname)
                .email(email)
                .passwordHash(passwordHash)
                .role(role != null ? role : UserRole.USER) // 기본값 처리
                .build();
    }

    // --- 의도 있는 변경 메서드(Setter 대체) ---
    public void changeNickname(String newNickname) {
        this.nickname = Objects.requireNonNull(newNickname);
    }

    public void changePasswordHash(String newHash) {
        this.passwordHash = Objects.requireNonNull(newHash);
    }

    public void changeRole(UserRole newRole) {
        this.role = Objects.requireNonNull(newRole);
    }

    public void changeEmail(String newEmail) {
        this.email = Objects.requireNonNull(newEmail,"이메일은 null일 수 없습니다");
    }

    public void changeProfileImage (String key) {       // 아바타 변경 메서드
        this.profileImageKey = key;
    }

    public void changeOffline(Long count) {
        this.offLine = count;
    }

    public void deleteUser(Boolean isDeleted,  String email) {
        this.deleted = isDeleted;
        this.nickname = "탈퇴한 사용자";
        this.email = null;
    }
}
