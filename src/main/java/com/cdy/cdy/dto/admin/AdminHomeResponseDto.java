package com.cdy.cdy.dto.admin;



public interface AdminHomeResponseDto {
    Long getId();
    String getName();
    String getProfileImage();
    Integer getOfflineCount();
    Integer getStudyCount();
    Integer getProjectCount();
}