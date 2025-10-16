package com.cdy.cdy.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CursorResponse<T> {
    private List<T> data;
    private Long nextCursor;
    private boolean hasNext;
}
