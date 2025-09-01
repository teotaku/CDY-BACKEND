package com.cdy.cdy.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PresignRequest {
    private String originalFilename; // ex) "IMG_0001.jpg"
    private String contentType;      // ex) "image/jpeg"

}