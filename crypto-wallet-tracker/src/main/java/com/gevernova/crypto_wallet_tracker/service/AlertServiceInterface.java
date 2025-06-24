package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.request.AlertRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.AlertResponseDTO;
import java.util.List;

public interface AlertServiceInterface {
    AlertResponseDTO createAlert(AlertRequestDTO dto, String email);
    List<AlertResponseDTO> getAlerts(String email);
    void evaluateAlerts();
}