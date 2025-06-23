package com.gevernova.crypto_wallet_tracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpLoginRequestDTO {

    @Email(message = "Invalid email format")
    private String email;

    private String password;  // Optional — used if method is "password"

    private String otp;       // Optional — used if method is "otp"

    @NotBlank(message = "Login method is required")
    private String method;    // Should be either "password" or "otp"
}