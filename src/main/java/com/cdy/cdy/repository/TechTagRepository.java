package com.cdy.cdy.repository;

import com.cdy.cdy.entity.TechTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechTagRepository extends JpaRepository<TechTag, Long> {
    Optional<TechTag> findByName(String name);
    boolean existsByName(String name);
}
