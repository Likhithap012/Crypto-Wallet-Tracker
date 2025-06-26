package com.gevernova.crypto_wallet_tracker.mapper;

import com.gevernova.crypto_wallet_tracker.dto.request.AlertRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.AlertResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.PriceAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class AlertMapper {
    public PriceAlert toEntity(AlertRequestDTO dto, String email) {
        log.debug("Mapping AlertRequestDTO to Entity for email={}: {}", email, dto);
        return PriceAlert.builder()
                .email(email)
                .coin(dto.getCoin())
                .targetPrice(dto.getTargetPrice())
                .condition(dto.getCondition())
                .triggered(false)
                .build();
    }

    public AlertResponseDTO toDTO(PriceAlert alert) {
        return AlertResponseDTO.builder()
                .id(alert.getId())
                .coin(alert.getCoin())
                .targetPrice(alert.getTargetPrice())
                .condition(alert.getCondition())
                .triggered(alert.isTriggered())
                .build();
    }
}