package com.cdy.cdy.service;

import com.cdy.cdy.dto.request.CreateStudyChannelRequest;
import com.cdy.cdy.dto.request.CreateStudyImageDto;
import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.dto.response.StudyChannelResponse;
import com.cdy.cdy.dto.response.StudyImageResponse;
import com.cdy.cdy.entity.StudyChannel;
import com.cdy.cdy.entity.StudyImage;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.repository.StudyChannelRepository;
import com.cdy.cdy.repository.StudyImageRepository;
import com.cdy.cdy.repository.UserRepository;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final StudyImageRepository studyImageRepository;
    private final R2StorageService r2StorageService;

    public StudyChannelResponse createStudy(Long userId, CreateStudyChannelRequest req) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        // 1) 스터디 글 저장
        StudyChannel study = StudyChannel.from(owner, req); // 기존 로직 그대로
        studyChannelRepository.save(study);

        // 2) 이미지 저장 (키만 저장)
        if (req.getImages() != null) {
            for (CreateStudyImageDto img : req.getImages()) {
                StudyImage si = StudyImage.builder()
                        .study(study)
                        .key(img.getKey())
                        .sortOrder(img.getSortOrder())
//                        .alt(img.getAlt())
                        .build();
                studyImageRepository.save(si);
            }
        }

        // 3) 응답 조립 (간단 버전)
        return StudyChannelResponse.from(study);
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

        // 이미지 조회
        List<StudyImage> images = studyImageRepository.findByStudyId(studyId);

        // key → url 변환 (퍼블릭이면 publicUrl, 프라이빗이면 presignGet)
        List<StudyImageResponse> imageResponses = images.stream()
                .map(img -> StudyImageResponse.from(
                        img,
                        r2StorageService.publicUrl(img.getKey()) // presignGet(img.getKey(), 300)도 가능
                ))
                .toList();

        return StudyChannelResponse.from(studyChannel, imageResponses);
    }

    // 3. 전체 조회
    public Page<StudyChannelResponse> getAllStudies(Pageable pageable) {

        return studyChannelRepository.findAll(pageable)
                .map(study -> {
                    List<StudyImage> images = studyImageRepository.findByStudyId(study.getId());
                    List<StudyImageResponse> imageResponses = images.stream()
                            .map(img -> StudyImageResponse.from(
                                    img,
                                    r2StorageService.publicUrl(img.getKey())
                            ))
                            .toList();
                    return StudyChannelResponse.from(study, imageResponses);
                });
    }


    public Page<StudyChannelResponse> findByCategory(Long id, String category, Pageable pageable) {

        return null;
    }
}



