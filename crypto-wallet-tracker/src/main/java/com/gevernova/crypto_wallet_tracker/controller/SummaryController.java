package com.gevernova.crypto_wallet_tracker.controller;

import com.gevernova.crypto_wallet_tracker.dto.response.SummaryResponseDTO;
import com.gevernova.crypto_wallet_tracker.service.SummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping
    public ResponseEntity<SummaryResponseDTO> getSummary(Authentication authentication) {
        String email = authentication.getName();
        log.info("Authenticated user email received from JWT: {}", email);

        SummaryResponseDTO summary = summaryService.getSummaryForUser(email);
        return ResponseEntity.ok(summary);
    }

}
