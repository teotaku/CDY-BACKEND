package com.cdy.cdy.dto.admin;

import java.time.LocalDateTime;

public interface UserInfoResponse {
    Long getId();
    String getName();
    String getPhoneNumber();
    String getEmail();
    String getPasswordHash();
    String getCategory();
    LocalDateTime getCreatedAt();
}
