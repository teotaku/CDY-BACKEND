package com.cdy.cdy.service;

import com.cdy.cdy.dto.request.CreateStudyChannelRequest;
import com.cdy.cdy.dto.response.StudyChannelResponse;
import com.cdy.cdy.entity.StudyChannel;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.repository.StudyChannelRepository;
import com.cdy.cdy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyChannelRepository studyChannelRepository;
    private final UserRepository userRepository;

    public StudyChannelResponse createStudy(Long userId, CreateStudyChannelRequest request) {


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을수 없습니다."));


        StudyChannel studyChannel = StudyChannel.from(user, request);

        return StudyChannelResponse.builder()
                .id(studyChannel.getId())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

    }

    public StudyChannelResponse getStudy(Long studyId) {
        StudyChannel studyChannel = studyChannelRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디 채널을 찾을 수 없습니다."));

        return StudyChannelResponse.from(studyChannel);
    }

    // 3. 전체 조회
    public List<StudyChannelResponse> getAllStudies() {
        List<StudyChannel> studies = studyChannelRepository.findAll();

        return studies.stream()
                .map(StudyChannelResponse::from)
                .toList();
    }

}



