package com.cdy.cdy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@AllArgsConstructor
@Builder
@Getter
@Table(name = "banners")
public class Banner {

    private Long id;
    private String imageKey;

}
