// src/main/java/com/cdy/cdy/repository/StudyImageRepository.java
package com.cdy.cdy.repository;

import com.cdy.cdy.entity.study.StudyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyImageRepository extends JpaRepository<StudyImage, Long> {
    List<StudyImage> findByStudyId(Long studyId);
}
