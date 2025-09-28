package com.cdy.cdy.dto.response.study;
import com.cdy.cdy.dto.response.MonthCalendarResponse;
import com.cdy.cdy.dto.response.project.CompleteProject;
import com.cdy.cdy.entity.project.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class DetailStudyChannelResponse {

    private String category;
    private String userImageUrl;
    private Long studyCount;


    //스터디목록들
    private PageResponse<ResponseStudyByUser> Studies;

    //완료된 프로젝트
    private List<CompleteProject> completedProject;

    private MonthCalendarResponse month;
}
