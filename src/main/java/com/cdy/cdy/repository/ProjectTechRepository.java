package com.cdy.cdy.repository;

import com.cdy.cdy.entity.ProjectTech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectTechRepository extends JpaRepository<ProjectTech,Long> {
}
