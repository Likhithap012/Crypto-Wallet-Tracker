package com.gevernova.crypto_wallet_tracker.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Data
@Builder
public class WalletRequestDTO {

    @NotBlank(message = "Coin symbol is required")
    private String coin;  // e.g., BTC, ETH

    @Min(value = 0, message = "Units must be non-negative")
    private Double units;  // Number of units owned

    @Min(value = 0, message = "Buy price must be non-negative")
    private Double buyPrice;  // Purchase price per unit
}
