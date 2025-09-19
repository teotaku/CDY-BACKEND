package com.cdy.cdy.repository;

import com.cdy.cdy.dto.response.study.SimpleStudyDto;
import com.cdy.cdy.entity.StudyChannel;
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
        select new com.cdy.cdy.dto.response.SimpleStudyDto(
            sc.id, u.id, u.profileImageUrl, u.category
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

}
