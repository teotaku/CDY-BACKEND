// com.cdy.cdy.dto.response.PresignResponse
package com.cdy.cdy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignResponse {
    private String key;        // 서버가 만든 업로드 대상 키(예: uploads/uuid.jpg)
    private String uploadUrl;  // 이 URL로 PUT 업로드
    private int    expiresIn;  // 초 단위 TTL(표시용)
}
