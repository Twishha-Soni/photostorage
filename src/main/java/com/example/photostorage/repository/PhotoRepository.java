package com.example.photostorage.repository;

import com.example.photostorage.dto.response.PhotoResponse;
import com.example.photostorage.entity.Album;
import com.example.photostorage.entity.Photo;
import com.example.photostorage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByUser(User user);
    Optional<Photo> findByPhotoIdAndUser(Long id, User user);
    List<Photo> findByAlbumAndUser(Album album, User user);
    List<Photo> findByUserAndAlbumIsNull(User user);
}
