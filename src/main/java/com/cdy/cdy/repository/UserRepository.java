package com.cdy.cdy.repository;


import com.cdy.cdy.dto.response.study.SimpleStudyDto;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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




}
