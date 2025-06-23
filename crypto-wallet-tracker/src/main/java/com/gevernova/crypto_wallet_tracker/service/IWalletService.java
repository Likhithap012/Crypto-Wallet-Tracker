package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.request.WalletRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.WalletResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IWalletService {
    WalletResponseDTO addEntry(WalletRequestDTO dto);
    List<WalletResponseDTO> getAllEntries();
    Optional<WalletResponseDTO> updateEntry(Long id, WalletRequestDTO updatedDTO);
    void deleteEntry(Long id);
}
