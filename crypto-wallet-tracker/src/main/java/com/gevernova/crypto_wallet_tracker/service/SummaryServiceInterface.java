package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.response.SummaryResponseDTO;

public interface SummaryServiceInterface {
    SummaryResponseDTO getSummaryForUser(String username);
}
