package com.gevernova.crypto_wallet_tracker.mapper;

import com.gevernova.crypto_wallet_tracker.dto.response.SummaryResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SummaryMapper {

    public SummaryResponseDTO toDTO(double totalInvestment, double currentValue) {
        double netGainLoss = currentValue - totalInvestment;
        double gainLossPercent = totalInvestment != 0 ? (netGainLoss / totalInvestment) * 100 : 0.0;

        return SummaryResponseDTO.builder()
                .totalInvestment(round(totalInvestment))
                .currentValue(round(currentValue))
                .netGainLoss(round(netGainLoss))
                .netGainLossPercent(round(gainLossPercent))
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
