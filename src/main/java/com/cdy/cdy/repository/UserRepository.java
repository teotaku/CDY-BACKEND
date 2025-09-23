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

    @Query("select new com.cdy.cdy.dto.response.study.SimpleStudyDto(u.id, u.profileImageKey, u.category) " +
            "from User u " +
            "where u.category = :category")
    Page<SimpleStudyDto> findByCategory(@Param("category") UserCategory category, Pageable pageable);
}
