package com.cdy.cdy.repository;

import com.cdy.cdy.entity.StudyChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyChannelRepository extends JpaRepository<StudyChannel,Long> {



}
