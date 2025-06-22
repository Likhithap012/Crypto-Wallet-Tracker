package com.gevernova.crypto_wallet_tracker.mapper;

import com.gevernova.crypto_wallet_tracker.dto.request.WalletRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.WalletResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.WalletEntry;
import org.springframework.stereotype.Component;

@Component
public class WalletEntryMapper {

    public WalletResponseDTO toResponseDTO(WalletEntry entity) {
        return WalletResponseDTO.builder()
                .coin(entity.getCoin())
                .units(entity.getUnits())
                .buyPrice(entity.getBuyPrice())
                .build();
    }

    public WalletEntry toEntity(WalletRequestDTO dto) {
        return WalletEntry.builder()
                .coin(dto.getCoin())
                .units(dto.getUnits())
                .buyPrice(dto.getBuyPrice())
                .build();
    }
}

