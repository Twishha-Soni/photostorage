package com.example.photostorage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter @AllArgsConstructor
public class AlbumResponse {

    private Long albumId;
    private String name;
    private String description;
    private LocalDate createdDate;
    private Long userId;
}
