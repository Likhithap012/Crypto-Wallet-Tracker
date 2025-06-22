package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.request.WalletRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.WalletResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.WalletEntry;
import com.gevernova.crypto_wallet_tracker.mapper.WalletEntryMapper;
import com.gevernova.crypto_wallet_tracker.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletEntryMapper mapper;

    public WalletResponseDTO addEntry(WalletRequestDTO dto) {
        WalletEntry entry = mapper.toEntity(dto);
        WalletEntry saved = walletRepository.save(entry);
        return mapper.toResponseDTO(saved);
    }

    public List<WalletResponseDTO> getAllEntries() {
        return walletRepository.findAll().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<WalletResponseDTO> updateEntry(Long id, WalletRequestDTO updatedDTO) {
        return walletRepository.findById(id).map(existing -> {
            existing.setCoin(updatedDTO.getCoin());
            existing.setUnits(updatedDTO.getUnits());
            existing.setBuyPrice(updatedDTO.getBuyPrice());
            WalletEntry saved = walletRepository.save(existing);
            return mapper.toResponseDTO(saved);
        });
    }

    public void deleteEntry(Long id) {
        walletRepository.deleteById(id);
    }
}

