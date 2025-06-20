package com.gevernova.crypto_wallet_tracker.dto.response;
// RegisterRequest
// LoginRequest
// Response DTOs = What the backend sends back to the client
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
    private String email;
}