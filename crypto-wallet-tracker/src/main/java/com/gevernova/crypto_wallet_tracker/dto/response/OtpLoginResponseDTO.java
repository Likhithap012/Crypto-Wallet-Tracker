package com.gevernova.crypto_wallet_tracker.dto.response;

// DTO for OTP login response

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpLoginResponseDTO {
    private String token;
    private String name;
    private String role;
    private String method; // "otp" or "password"
}