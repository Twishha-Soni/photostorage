package com.example.photostorage.service;

import com.example.photostorage.config.StorageConfig;
import com.example.photostorage.dto.response.PhotoResponse;
import com.example.photostorage.entity.Photo;
import com.example.photostorage.entity.User;
import com.example.photostorage.exception.BadRequestException;
import com.example.photostorage.exception.ResourceNotFoundException;
import com.example.photostorage.repository.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final UserService userService;
    private final StorageConfig storageConfig;

    public PhotoService(PhotoRepository photoRepository, UserService userService, StorageConfig storageConfig) {
        this.photoRepository = photoRepository;
        this.userService = userService;
        this.storageConfig = storageConfig;
    }

    public PhotoResponse upload(MultipartFile file) {
        validateFile(file);

        User currentUser = userService.getCurrentUser();

        // Build a safe unique filename
        String extension = getExtension(file.getOriginalFilename());
        String safeFilename = System.currentTimeMillis() + "_" + UUID.randomUUID() + extension;

        // Store under a pre-user subdirectory to keep things organised
        String relativePath = "user_" + currentUser.getUserId() + "/" + safeFilename;
        Path destination = Paths.get(storageConfig.getUploadDir(), relativePath);

        try {
            // Ensure the pre-user directory exists
            Files.createDirectories(destination.getParent());
            // Stream bytes from multipart part to disk
            file.transferTo(destination);
        } catch(IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }

        Photo photo = new Photo();
        photo.setFilename(safeFilename);
        photo.setOriginalFilename(file.getOriginalFilename());
        photo.setContentType(file.getContentType());
        photo.setFileSize(file.getSize());
        photo.setUploadDate(LocalDateTime.now());
        photo.setFilepath(relativePath);
        photo.setUser(currentUser);
        // album is null - unorganized for now

        return toResponse(photoRepository.save(photo));
    }

    public List<PhotoResponse> getMyPhotos() {
        User currentUser = userService.getCurrentUser();

        return photoRepository.findByUser(currentUser)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public  PhotoResponse getPhotoMetadata(Long photoId) {
        User currentUser = userService.getCurrentUser();
        Photo photo = photoRepository.findByPhotoIdAndUser(photoId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));

        return toResponse(photo);
    }

    public void delete(Long photoId) {
        User currentUser = userService.getCurrentUser();
        Photo photo = photoRepository.findByPhotoIdAndUser(photoId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));

        // Delete file from disk
        Path filePath = Paths.get(storageConfig.getUploadDir(), photo.getFilepath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete the file from disk: " + e.getMessage() ,e);
        }

        photoRepository.delete(photo);
    }

    // ---- private helper functions ----
    public void validateFile(MultipartFile file) {
        if(file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }

        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }

        long maxSize = 10L * 1024 * 1024; // 10MB
        if(file.getSize() > maxSize) {
            throw new BadRequestException("File size must not exceed 10MB");
        }
    }

    private String getExtension(String originalFilename) {
        if(originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
        // eg ".jpg", ".png"
    }

    private PhotoResponse toResponse(Photo p) {
        return new PhotoResponse(
                p.getPhotoId(),
                p.getOriginalFilename(),
                p.getContentType(),
                p.getFileSize(),
                p.getUploadDate(),
                p.getUser().getUserId(),
                p.getAlbum() != null ? p.getAlbum().getAlbumId() : null
        );
    }

    // package - private (default)
    Photo findOwnedPhotoOrThrow(Long photoId, User user) {
        return photoRepository.findByPhotoIdAndUser(photoId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));
    }
}
