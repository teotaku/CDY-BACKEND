package com.cdy.cdy.dto.response.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;     // 실제 데이터
    private int pageNumber;      // 현재 페이지 번호
    private int pageSize;        // 페이지 크기
    private long totalElements;  // 전체 개수
    private int totalPages;      // 전체 페이지 수
    private boolean last;        // 마지막 페이지 여부

    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
