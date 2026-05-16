package com.example.photostorage.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min=3, max=30, message = "Username must be between 3 to 30 characters long")
    private String username;

    @Email(message = "Must be a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min=6, message = "Password must be at least 6 characters long")
    private String password;
}
