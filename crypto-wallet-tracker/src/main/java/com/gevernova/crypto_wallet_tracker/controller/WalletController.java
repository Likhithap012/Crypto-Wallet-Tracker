package com.gevernova.crypto_wallet_tracker.controller;

import com.gevernova.crypto_wallet_tracker.dto.request.WalletRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.WalletResponseDTO;
import com.gevernova.crypto_wallet_tracker.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponseDTO> addEntry(@Valid @RequestBody WalletRequestDTO dto) {
        return ResponseEntity.ok(walletService.addEntry(dto));
    }

    @GetMapping
    public ResponseEntity<List<WalletResponseDTO>> getAll() {
        return ResponseEntity.ok(walletService.getAllEntries());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WalletResponseDTO> update(@PathVariable Long id, @Valid @RequestBody WalletRequestDTO dto) {
        return walletService.updateEntry(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        walletService.deleteEntry(id);
        return ResponseEntity.ok("Entry deleted successfully");
    }
}

