package com.example.photostorage.service;

import com.example.photostorage.dto.request.AlbumRequest;
import com.example.photostorage.dto.response.AlbumResponse;
import com.example.photostorage.entity.Album;
import com.example.photostorage.entity.User;
import com.example.photostorage.exception.ResourceNotFoundException;
import com.example.photostorage.repository.AlbumRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final UserService userService;

    public AlbumService(AlbumRepository albumRepository, UserService userService) {
        this.albumRepository = albumRepository;
        this.userService = userService;
    }

    public AlbumResponse create(AlbumRequest request) {
        User currentUser = userService.getCurrentUser();

        Album album = new Album();
        album.setName(request.getName());
        album.setDescription(request.getDescription());
        album.setCreatedDate(LocalDate.now());
        album.setUser(currentUser);

        return toResponse(albumRepository.save(album));
    }

    public List<AlbumResponse> getMyAlbums() {
        User currentUser = userService.getCurrentUser();
        return albumRepository.findByUser(currentUser)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AlbumResponse getById(Long id) {
        User currentUser = userService.getCurrentUser();
        Album album = albumRepository.findByAlbumIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + id));

        return toResponse(album);
    }

    public AlbumResponse update(Long id, AlbumRequest request) {
        User currentUser = userService.getCurrentUser();
        Album album = albumRepository.findByAlbumIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + id));

        album.setName(request.getName());
        album.setDescription(request.getDescription());

        return toResponse(albumRepository.save(album));
    }

    public void delete(Long id) {
        User currentUser = userService.getCurrentUser();
        Album album = albumRepository.findByAlbumIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + id));

        albumRepository.delete(album);
    }


    // ---- private helper function ----
    private AlbumResponse toResponse(Album album) {
        return new AlbumResponse(
                album.getAlbumId(),
                album.getName(),
                album.getDescription(),
                album.getCreatedDate(),
                album.getUser().getUserId()
        );
    }
}
