package com.cdy.cdy.repository;

import com.cdy.cdy.dto.response.study.SimpleStudyDto;
import com.cdy.cdy.entity.study.StudyChannel;
import com.cdy.cdy.entity.UserCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyChannelRepository extends JpaRepository<StudyChannel,Long> {

    @Query(value = """
        select new com.cdy.cdy.dto.response.study.SimpleStudyDto(
           u.id, u.profileImageKey, u.category
        )
        from StudyChannel sc
        join sc.owner u
        where u.category = :category
        """,
            countQuery = """
        select count(sc)
        from StudyChannel sc
        join sc.owner u
        where u.category = :category
        """)
    Page<SimpleStudyDto> findByUserCategorySimple(@Param("category") UserCategory category,
                                                  Pageable pageable);


    @Query("SELECT sc FROM StudyChannel sc WHERE sc.owner.id = :userId")
    Page<StudyChannel> findUserStudies(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT COUNT(sc) FROM StudyChannel sc WHERE sc.owner.id = :userId")
    Long getStudyCount(@Param("userId") Long userId);



}
