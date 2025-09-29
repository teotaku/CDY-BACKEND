package com.cdy.cdy.entity;

import java.util.Arrays;

public enum UserCategory {

    CODING, //코딩
    DESIGN,
    VIDEO_EDITING;

    public static UserCategory from(String value) {
        return Arrays.stream(UserCategory.values())
                .filter(c -> c.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리: " + value));
    }

}
