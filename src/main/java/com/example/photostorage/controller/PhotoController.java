package com.example.photostorage.controller;

import com.example.photostorage.dto.response.PhotoResponse;
import com.example.photostorage.service.PhotoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PhotoResponse> upload(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(photoService.upload(file));
    }

    @GetMapping
    public ResponseEntity<List<PhotoResponse>> getMyPhotos() {
        return ResponseEntity.ok(photoService.getMyPhotos());
    }

    @GetMapping("/{id}/metadata")
    public ResponseEntity<PhotoResponse> getPhotoMetadata(@PathVariable Long id) {
        return ResponseEntity.ok(photoService.getPhotoMetadata(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        photoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        return photoService.download(id);
    }
}
