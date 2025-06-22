package com.gevernova.crypto_wallet_tracker.dto.response;
// Returns overall portfolio metrics
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryResponseDTO {
    private Double totalInvestment;
    private Double currentValue;
    private Double netGainLoss;
    private Double netGainLossPercent;
}