package com.cdy.cdy.repository;


import com.cdy.cdy.dto.admin.AdminHomeResponseDto;
import com.cdy.cdy.dto.admin.UserInfoResponse;
import com.cdy.cdy.dto.response.study.SimpleStudyDto;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String userEmail);

    Boolean existsByEmail(String userEmail);


    boolean existsByNicknameAndIdNot(String newNickname, Long userId);

    boolean existsByEmailAndIdNot(String newEmail, Long userId);

//    //단순 카테고리별 조회
//    @Query("select new com.cdy.cdy.dto.response.study.SimpleStudyDto(u.id, u.profileImageKey, u.category) " +
//            "from User u " +
//            "where u.category = :category")
//    Page<SimpleStudyDto> findByCategory(@Param("category") UserCategory category, Pageable pageable);


    //카테고리별 스터디 최신글 작성한 순서대로 유저정보 조회

    // ✅ 카테고리별 최신 스터디 작성순 정렬 (JPQL 버전)
    @Query(
            value = """
        SELECT 
           u.id AS userId,
           u.avatar_key AS userImage,
           u.category AS category
        FROM users u
        LEFT JOIN study_channels s ON s.owner_id = u.id
        WHERE u.category = :category
        GROUP BY u.id, u.avatar_key, u.category
        ORDER BY MAX(s.created_at) DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT u.id)
        FROM users u
        LEFT JOIN study_channels s ON s.owner_id = u.id
        WHERE u.category = :category
        """,
            nativeQuery = true
    )
    Page<SimpleStudyDto> findByCategoryOrderByLatestStudyNative(
            @Param("category")String category,
            Pageable pageable
    );


    //이름과 이메일로 유저 정보 찾아오기
    @Query("SELECT u FROM User u WHERE u.name = :name AND u.email = :email")
    Optional<User> findByNameAndEmail(@Param("name") String name, @Param("email") String email);

    @Query(value = """
SELECT 
    u.id AS id,
    u.name AS name,
    u.avatar_key AS profileImage,
    u.off_line AS offlineCount,  -- ✅ 오프라인 참여 횟수 추가
    COALESCE(sc.study_count, 0) AS studyCount,
    COALESCE(pm.project_count, 0) AS projectCount
FROM users u
LEFT JOIN (
    SELECT owner_id, COUNT(*) AS study_count
    FROM study_channels
    GROUP BY owner_id
) sc ON u.id = sc.owner_id
LEFT JOIN (
    SELECT user_id, COUNT(*) AS project_count
    FROM project_members
    WHERE status = 'COMPLICATED'
    GROUP BY user_id
) pm ON u.id = pm.user_id
WHERE (:lastUserId IS NULL OR u.id < :lastUserId)
AND u.deleted = 0
ORDER BY u.id DESC
LIMIT :limit
""", nativeQuery = true)
    List<AdminHomeResponseDto> findHomeData(
            @Param("lastUserId") Long lastUserId,
            @Param("limit") int limit
    );


    @Query(value = """
        SELECT 
            id AS id,
            name AS name,
            phone_number AS phoneNumber,
            email AS email,
            password_hash AS passwordHash,
            category AS category,
            created_at AS createdAt
        FROM users
        WHERE id <= :lastUserId
        AND users.deleted = 0
        ORDER BY id DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<UserInfoResponse> getUserInfoList(@Param("lastUserId") Long lastUserId,
                                           @Param("limit") int limit);


    @Query(value =
            """
                    SELECT * FROM
                    USER u
                    JOIN study_channels s ON s.owner_id = u.id
                    WHERE s.id = :studyId
                    """,nativeQuery = true

    )
    Optional<User> findByStudyID(@Param("studyId")  Long studyId);


    @Query(value = "SELECT MAX(id) FROM users", nativeQuery = true)
    Long findMaxId();
}

