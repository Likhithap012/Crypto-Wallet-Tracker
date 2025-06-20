package com.gevernova.crypto_wallet_tracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
// DTO for user registration request.

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;  // User's full name

    @Email(message = "Invalid email")
    private String email;  // User's email (must be valid format)

    @NotBlank(message = "Password is required")
    private String password;  // User's password (not empty)
}
