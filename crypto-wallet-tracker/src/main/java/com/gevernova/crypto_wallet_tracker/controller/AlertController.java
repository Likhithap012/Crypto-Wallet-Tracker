package com.gevernova.crypto_wallet_tracker.controller;

import com.gevernova.crypto_wallet_tracker.dto.request.AlertRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.AlertResponseDTO;
import com.gevernova.crypto_wallet_tracker.service.AlertServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertServiceInterface alertService;

    @PostMapping
    public ResponseEntity<AlertResponseDTO> createAlert(@RequestBody AlertRequestDTO dto,
                                                        Authentication authentication) {
        String email = authentication.getName();
        log.info("Creating alert for user {}: {}", email, dto);
        return ResponseEntity.ok(alertService.createAlert(dto, email));
    }

    @GetMapping
    public ResponseEntity<List<AlertResponseDTO>> getAlerts(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetching alerts for user {}", email);
        return ResponseEntity.ok(alertService.getAlerts(email));
    }

}
