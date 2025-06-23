package com.gevernova.crypto_wallet_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordResponseDTO {
    private String message;
    private String email;
    private boolean success;
}