package com.gevernova.crypto_wallet_tracker.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertResponseDTO {
    private Long id;
    private String coin;
    private double targetPrice;
    private String condition;
    private boolean triggered;

}
