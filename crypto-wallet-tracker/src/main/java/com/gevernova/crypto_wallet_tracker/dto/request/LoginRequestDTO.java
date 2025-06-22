package com.gevernova.crypto_wallet_tracker.dto.request;

// Request DTO = Data sent from client during login

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @Email(message = "Invalid email")
    private String email;  // User's email (must be valid)

    @NotBlank(message = "Password is required")
    private String password;  // User's password (not empty)
}
