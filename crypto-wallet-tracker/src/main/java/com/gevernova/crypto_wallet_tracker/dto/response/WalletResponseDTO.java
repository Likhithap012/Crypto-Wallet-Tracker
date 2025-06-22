package com.gevernova.crypto_wallet_tracker.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponseDTO {
    private String coin;
    private Double units;
    private Double buyPrice;
}
