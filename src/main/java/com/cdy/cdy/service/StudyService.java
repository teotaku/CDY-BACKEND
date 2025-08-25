package com.cdy.cdy.service;

import com.cdy.cdy.dto.request.CreateStudyChannelRequest;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.StudyChannelResponse;
import com.cdy.cdy.entity.StudyChannel;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.repository.StudyChannelRepository;
import com.cdy.cdy.repository.UserRepository;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyChannelRepository studyChannelRepository;
    private final UserRepository userRepository;

    public StudyChannelResponse createStudy(Long userID ,
                                             CreateStudyChannelRequest request) {


        User user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을수 없습니다."));


        StudyChannel studyChannel = StudyChannel.from(user, request);

        return StudyChannelResponse.builder()
                .id(studyChannel.getId())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

    }

    //스터디 수정

    public void updateStudy(Long studyId,
                            CreateStudyChannelRequest studyChannelRequest) {
        StudyChannel studyChannel = studyChannelRepository.findById(studyId)
                .orElseThrow(()-> new IllegalArgumentException("해당 스터디 채널을 찾을 수 없습니다."));


        studyChannel.update(studyChannelRequest);
            }

      //스터디 삭제

    public void deleteStudy(
            Long studyId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."
                ));

        StudyChannel studyChannel = studyChannelRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));

        if (!studyChannel.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("스터디 삭제 권한이 없습니다.");
        }
        studyChannelRepository.delete(studyChannel);



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



