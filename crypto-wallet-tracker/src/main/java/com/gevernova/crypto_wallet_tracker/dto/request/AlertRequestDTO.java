package com.gevernova.crypto_wallet_tracker.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRequestDTO {
    private String coin;
    private double targetPrice;
    private String condition; // e.g., "ABOVE" or "BELOW"
}