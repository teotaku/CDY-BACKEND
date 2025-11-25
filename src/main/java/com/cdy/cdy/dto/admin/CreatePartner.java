package com.cdy.cdy.dto.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreatePartner {


    private String name;
    private String imageKey;
}
