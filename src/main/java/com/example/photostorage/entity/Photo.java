package com.example.photostorage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
@Getter @Setter @NoArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    @Column(nullable = false)
    private String filename;  // generated safe name on disk

    @Column(nullable = false)
    private String originalFilename; // from user defined name

    @Column(nullable = false)
    private String contentType;  // jpeg, jpg, etc

    @Column(nullable = false)
    private Long fileSize;  // bytes

    @Column(nullable = false)
    private LocalDateTime uploadDate;

    @Column(nullable = false)
    private String filepath;     // relative path from upload base dir

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = true)
    private Album album;     // nullable - photo can be unorganized
}
