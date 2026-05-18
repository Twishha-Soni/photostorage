package com.example.photostorage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class PhotoResponse {

    private Long photoId;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private Long userId;
    private Long albumId;
}
