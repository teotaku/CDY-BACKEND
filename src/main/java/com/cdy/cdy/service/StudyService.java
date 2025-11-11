package com.cdy.cdy.service;

import com.cdy.cdy.dto.request.CreateStudyChannelRequest;
import com.cdy.cdy.dto.request.CreateStudyImageDto;
import com.cdy.cdy.dto.request.UpdateStudyChannelRequest;
import com.cdy.cdy.dto.request.UpdateStudyImageDto;
import com.cdy.cdy.dto.response.MonthCalendarResponse;
import com.cdy.cdy.dto.response.study.StudyChannelResponse;
import com.cdy.cdy.dto.response.StudyImageResponse;
import com.cdy.cdy.dto.response.project.CompleteProject;
import com.cdy.cdy.dto.response.study.*;
import com.cdy.cdy.entity.project.Project;
import com.cdy.cdy.entity.study.StudyChannel;
import com.cdy.cdy.entity.study.StudyImage;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserCategory;
import com.cdy.cdy.repository.ProjectMemberRepository;
import com.cdy.cdy.repository.StudyChannelRepository;
import com.cdy.cdy.repository.StudyImageRepository;
import com.cdy.cdy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyChannelRepository studyChannelRepository;
    private final UserRepository userRepository;
    private final StudyImageRepository studyImageRepository;
    private final R2StorageService r2StorageService;
    private final AttendanceService attendanceService;
    private final ImageUrlResolver imageUrlResolver;
    private final ProjectMemberRepository projectMemberRepository;


    //스터디 생성
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
                        .build();
                studyImageRepository.save(si);
            }
        }
        attendanceService.checkToday(userId);

        // 3) 이미지 다시 조회해서 응답 DTO 조립 (⭐ from 제거 → builder로 직접 생성)
        List<StudyImage> images = studyImageRepository.findByStudyId(study.getId());

        List<StudyImageResponse> imageResponses = images.stream()
                .map(img -> StudyImageResponse.builder()      // 🔥 변경: from → builder
                        .url(r2StorageService.presignGet(img.getKey(), 3600).toString())  // presign url 생성 함수 필요
                        .sortOrder(img.getSortOrder())
                        .build()
                )
                .toList();

        return StudyChannelResponse.builder()                 // 🔥 변경: from → builder
                .id(study.getId())
                .content(study.getContent())
                .createdAt(study.getCreatedAt())
                .images(imageResponses)
                .build();
    }

    //스터디 수정
    @Transactional
    public void updateStudy(Long studyId,
                            Long userId,
                            UpdateStudyChannelRequest studyChannelRequest) {
        StudyChannel studyChannel = studyChannelRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디 채널을 찾을 수 없습니다."));

        if (!studyChannel.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("스터디 수정 권한이 없습니다.");
        }


        studyChannel.update(studyChannelRequest);

        if (studyChannelRequest.getImages() != null) {
            List<StudyImage> newImages = new ArrayList<>();
            int order = 1;
            for (UpdateStudyImageDto dto : studyChannelRequest.getImages()) {
                StudyImage img = StudyImage.builder()
                        .study(studyChannel)
                        .key(dto.getKey())
                        .sortOrder(order++)
                        .build();
                newImages.add(img);
            }
            studyChannel.replaceImages(newImages);
        }

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

    //단건조회
    public StudyChannelResponse getStudy(Long studyId) {
        StudyChannel studyChannel = studyChannelRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디 채널을 찾을 수 없습니다."));

        // 이미지 조회
        List<StudyImage> images = studyImageRepository.findByStudyId(studyId);


        // key → url 변환 (퍼블릭이면 publicUrl, 프라이빗이면 presignGet)
        List<StudyImageResponse> list = images.stream()
                .sorted(Comparator.comparingInt(StudyImage::getSortOrder))
                .map(img -> StudyImageResponse.builder()
                        .url(r2StorageService.presignGet(img.getKey(), 3600).toString())
                        .sortOrder(img.getSortOrder())
                        .build())
                .toList();

        StudyChannelResponse build = StudyChannelResponse.builder()
                .id(studyChannel.getId())
                .content(studyChannel.getContent())
                .createdAt(studyChannel.getCreatedAt())
                .images(list)
                .build();
        return build;

    }

    //카테고리별 조회
    public GroupedStudiesResponse getStudiesGrouped(Pageable codingPageable,
                                                    Pageable designPageable,
                                                    Pageable videoPageable) {


        Page<SimpleStudyDto> coding = userRepository.findByCategoryOrderByLatestStudyNative
                        (UserCategory.CODING.name(), codingPageable)
                .map(this::applyPresign);
        Page<SimpleStudyDto> design = userRepository.findByCategoryOrderByLatestStudyNative
                        (UserCategory.DESIGN.name(), designPageable)
                .map(this::applyPresign);
        Page<SimpleStudyDto> video  = userRepository.findByCategoryOrderByLatestStudyNative
                        (UserCategory.VIDEO_EDITING.name(), videoPageable)
                .map(this::applyPresign);

        return GroupedStudiesResponse.builder()
                .coding(coding)
                .design(design)
                .video(video)
                .build();
    }

    /** presign 변환 로직 👉 여기에 넣으면 됨 */
    private SimpleStudyDto applyPresign(SimpleStudyDto dto) {

        return SimpleStudyDto.builder()
                .userId(dto.getUserId())
                .userImage(imageUrlResolver.toPresignedUrl(dto.getUserImage())) // presign된 URL로 교체
                .category(dto.getCategory())
                .build();
    }

    //유저의 스터디목록 전체조회
    public Page<ResponseStudyByUser> findStudiesByUser(Long id, Pageable pageable) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        Page<StudyChannel> userStudies = studyChannelRepository.findUserStudies(id, pageable);

        List<ResponseStudyByUser> list = userStudies.stream().map(us -> ResponseStudyByUser.builder()
                        .studyId(us.getId())
                        .firstImage(
                                us.getImages().isEmpty()
                                        ? null
                                :imageUrlResolver.toPresignedUrl(us.getImages().get(0).getKey()))
                        .content(us.getContent())
                        .build())
                .toList();

        return new PageImpl<>(list, pageable, userStudies.getTotalElements());

    }


    //유저의 상세 스터디채널 조회
    public DetailStudyChannelResponse findStudyChannel(Long userId,Pageable StudyPageable) {

        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));

        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        YearMonth ym = YearMonth.from(now);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        String category = user.getCategory().toString();

        Long studyCount = studyChannelRepository.getStudyCount(userId);

        MonthCalendarResponse month = attendanceService.getMonth(userId, ym);

        Page<ResponseStudyByUser> studies = findStudiesByUser(userId, StudyPageable);


        List<Project> userCompletedProjects = projectMemberRepository.findUserCompletedProjects(userId);

        List<CompleteProject> completeProjectList = userCompletedProjects.stream().map(project -> CompleteProject.builder()
                        .id(project.getId())
                        .logoImageURL(imageUrlResolver.toPresignedUrl(project.getLogoImageKey()))
                        .build())
                .toList();


        //스터디목록조회
        Page<StudyChannel> userStudies = studyChannelRepository.findUserStudies(userId, StudyPageable);

        //dto변환,firstimage삽입
        List<ResponseStudyByUser> list = userStudies.stream().map(us -> ResponseStudyByUser.builder()
                        .studyId(us.getId())
                        .firstImage(us.getImages().isEmpty()
                                ? null
                                : imageUrlResolver.toPresignedUrl(us.getImages().get(0).getKey()))
                        .content(us.getContent())
                        .build())
                .toList();
        PageImpl<ResponseStudyByUser> studyPage =
                new PageImpl<>(list, StudyPageable, userStudies.getTotalElements());


        return DetailStudyChannelResponse.builder()
                .category(category)
                .userImageUrl(imageUrlResolver.toPresignedUrl(user.getProfileImageKey()))
                .Studies(PageResponse.from(studyPage))
                .completedProject(completeProjectList)
                .studyCount(studyCount)
                .month(month)
                .build();
    }





}

