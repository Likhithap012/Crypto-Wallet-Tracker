package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.request.AlertRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.AlertResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.PriceAlert;
import com.gevernova.crypto_wallet_tracker.mapper.AlertMapper;
import com.gevernova.crypto_wallet_tracker.repository.CoinPriceRepository;
import com.gevernova.crypto_wallet_tracker.repository.PriceAlertRepository;
import com.gevernova.crypto_wallet_tracker.util.jms.AlertEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertService implements AlertServiceInterface {

    private final PriceAlertRepository alertRepo;
    private final CoinPriceRepository coinPriceRepo;
    private final AlertMapper alertMapper;
    private final AlertEmailService emailService;


    @Override
    public AlertResponseDTO createAlert(AlertRequestDTO dto, String email) {
        PriceAlert alert = alertMapper.toEntity(dto, email);
        return alertMapper.toDTO(alertRepo.save(alert));
    }

    @Override
    public List<AlertResponseDTO> getAlerts(String email) {
        return alertRepo.findByEmail(email).stream()
                .map(alertMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(fixedRate = 10000)
    public void evaluateAlerts() {
        log.info("Evaluating alerts...");

        List<PriceAlert> alerts = alertRepo.findByTriggeredFalse();
        log.info("Fetched {} non-triggered alerts from DB", alerts.size());

        for (PriceAlert alert : alerts) {
            String coin = alert.getCoin().toLowerCase();
            double targetPrice = alert.getTargetPrice();
            String condition = alert.getCondition();

            double marketPrice = coinPriceRepo.findByCoinSymbol(coin)
                    .map(p -> p.getCurrentPrice())
                    .orElse(0.0);

            log.info("Evaluating alert for {}: condition={}, target={}, market={}", coin, condition, targetPrice, marketPrice);

            boolean conditionMet = (condition.equalsIgnoreCase("ABOVE") && marketPrice >= targetPrice)
                    || (condition.equalsIgnoreCase("BELOW") && marketPrice <= targetPrice);

            if (conditionMet) {
                alert.setTriggered(true);
                alertRepo.save(alert);

                log.info("Trigger met! Sending alert email for {} to {}", coin, alert.getEmail());
                emailService.sendAlertEmail(alert.getEmail(), coin, targetPrice, marketPrice);
            } else {
                log.info("Condition not met for {} (condition: {}, market: {}, target: {})", coin, condition, marketPrice, targetPrice);
            }
        }

        log.info("Alert evaluation completed.");

    }
}