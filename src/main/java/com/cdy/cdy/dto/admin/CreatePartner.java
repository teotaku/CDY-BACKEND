package com.cdy.cdy.dto.admin;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePartner {


    private String name;
    private String imageKey;
    private String link;
}
