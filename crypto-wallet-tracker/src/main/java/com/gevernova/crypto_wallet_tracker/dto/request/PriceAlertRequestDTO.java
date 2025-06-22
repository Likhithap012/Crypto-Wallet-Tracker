package com.gevernova.crypto_wallet_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO for creating a new price alert.

@Data
public class PriceAlertRequestDTO {

    @NotBlank(message = "Coin symbol is required")
    private String coinSymbol;  // e.g., BTC, ETH

    @NotNull(message = "Target price is required")
    private Double targetPrice;  // Threshold value to trigger alert

    @NotNull(message = "isGreaterThan flag is required")
    private Boolean isGreaterThan;  // true: alert when price > target, false: price < target
}
