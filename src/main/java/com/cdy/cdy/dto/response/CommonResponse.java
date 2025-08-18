package com.cdy.cdy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * API 성공 응답을 팀 표준 형태로 감싸는 래퍼.
 * - 상태코드는 ResponseEntity에서 관리(200, 201, 204 등)
 * - 바디 구조는 항상 동일(success, data, message, timestamp)
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 JSON에서 제외
public final class CommonResponse<T> {      // <T> = 응답 데이터의 타입 매개변수(제네릭)

    private final String timestamp; // 응답 생성 시각(ISO-8601 문자열)
    private final boolean success;  // 성공 여부(성공만 쓰지만 true 고정으로 명시)
    private final T data;           // 실제 페이로드(어떤 타입이든 들어갈 수 있음)
    private final String message;   // 선택적 메시지(성공 안내문 등)

    // 외부에서 new 못 하게 막고, 정해진 생성 규칙만 쓰도록 private
    private CommonResponse(boolean success, T data, String message) {
        this.timestamp = Instant.now().toString(); // 생성 시점 기록
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // ---- 정적 팩토리 메서드들(성공용) ----

    /** 데이터가 있을 때 */
    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(true, data, null);
    }

    /** 데이터 없이 성공만 알릴 때(예: 삭제 성공) */
    public static CommonResponse<Void> ok() {
        return new CommonResponse<>(true, null, null);
    }

    /** 데이터 + 메시지 같이 보낼 때 */
    public static <T> CommonResponse<T> okMsg(T data, String message) {
        return new CommonResponse<>(true, data, message);
    }

    // ---- 직렬화용 Getter (Jackson은 getter를 보고 JSON을 만든다) ----
    public String getTimestamp() { return timestamp; }
    public boolean isSuccess() { return success; } // boolean은 is* 네이밍
    public T getData() { return data; }
    public String getMessage() { return message; }
}

