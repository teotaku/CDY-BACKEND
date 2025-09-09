package com.cdy.cdy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantCardResponse {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private List<AnswerItem> answers;

    @Getter
    @AllArgsConstructor
    public static class AnswerItem {
        private Long questionId;
        private String question;   // 질문 텍스트(불필요하면 필드/매핑 제거)
        private String answerText; // 지원자 답
    }
}