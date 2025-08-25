package com.cdy.cdy.repository;


import com.cdy.cdy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String userEmail);

    Boolean existsByEmail(String userEmail);


    boolean existsByNicknameAndIdNot(String newNickname, Long userId);

    boolean existsByEmailAndIdNot(String newEmail, Long userId);
}
