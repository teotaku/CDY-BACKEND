// src/main/java/com/cdy/cdy/repository/ProjectAnswerRepository.java
package com.cdy.cdy.repository;

import com.cdy.cdy.entity.ProjectAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectAnswerRepository extends JpaRepository<ProjectAnswer, Long> {

    List<ProjectAnswer> findByMember_Id(Long memberId);
    Optional<ProjectAnswer> findByMember_IdAndQuestion_Id(Long memberId, Long questionId);

}
