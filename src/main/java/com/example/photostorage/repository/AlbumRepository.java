package com.example.photostorage.repository;

import com.example.photostorage.dto.response.AlbumResponse;
import com.example.photostorage.entity.Album;
import com.example.photostorage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByUser(User user);

    Optional<Album> findByAlbumIdAndUser(Long id, User user);
}
