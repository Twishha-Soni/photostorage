package com.example.photostorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AlbumRequest {

    @NotBlank(message = "Album name is required")
    @Size(max = 100, message = "Album name cannot exceed 100 characters length")
    private String name;

    @Size(max = 250, message = "Description cannot exceed 250 characters length")
    private String description;
}
