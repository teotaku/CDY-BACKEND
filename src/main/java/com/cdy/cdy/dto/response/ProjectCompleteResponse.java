package com.cdy.cdy.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProjectCompleteResponse {
    private boolean success;
    private String status;  // "WAITING", "COMPLETED", "ALREADY_COMPLETED"
    private String message;
    private Data data;

    @Builder
    @Getter
    public static class Data {
        private String userRole; // "MEMBER" or "LEADER"
        private int completedMembers;
        private int totalMembers;
        private double completionRate;
    }
}
