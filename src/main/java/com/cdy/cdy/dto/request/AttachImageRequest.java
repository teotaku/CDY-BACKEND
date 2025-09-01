package com.cdy.cdy.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttachImageRequest {
    private String key;        // presign 때 받은 key
    private String caption;    // 선택
}