package com.cdy.cdy.repository;


import com.cdy.cdy.entity.ProjectQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectQuestionRepository extends JpaRepository<ProjectQuestion,Long> {
}
