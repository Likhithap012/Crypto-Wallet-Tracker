package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.request.WalletRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.WalletResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.User;
import com.gevernova.crypto_wallet_tracker.entity.WalletEntry;
import com.gevernova.crypto_wallet_tracker.mapper.WalletEntryMapper;
import com.gevernova.crypto_wallet_tracker.repository.UserRepository;
import com.gevernova.crypto_wallet_tracker.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService implements WalletServiceInterface {

    private final WalletRepository walletRepository;
    private final WalletEntryMapper mapper;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public WalletResponseDTO addEntry(WalletRequestDTO dto) {
        User user = getCurrentUser();
        WalletEntry entry = mapper.toEntity(dto);
        entry.setUser(user); //  connect wallet to current user
        WalletEntry saved = walletRepository.save(entry);
        return mapper.toResponseDTO(saved);
    }

    public List<WalletResponseDTO> getAllEntries() {
        User user = getCurrentUser();
        return walletRepository.findByUser(user).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<WalletResponseDTO> updateEntry(Long id, WalletRequestDTO updatedDTO) {
        User user = getCurrentUser();
        return walletRepository.findById(id)
                .filter(e -> e.getUser() != null && e.getUser().getId().equals(user.getId()))
                .map(existing -> {
                    existing.setCoin(updatedDTO.getCoin());
                    existing.setUnits(updatedDTO.getUnits());
                    existing.setBuyPrice(updatedDTO.getBuyPrice());
                    WalletEntry saved = walletRepository.save(existing);
                    return mapper.toResponseDTO(saved);
                });
    }

    public void deleteEntry(Long id) {
        User user = getCurrentUser();
        WalletEntry entry = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));
        if (!entry.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        walletRepository.deleteById(id);
    }
}
