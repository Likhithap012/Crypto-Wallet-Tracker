package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.request.AlertRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.AlertResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;
import com.gevernova.crypto_wallet_tracker.entity.PriceAlert;
import com.gevernova.crypto_wallet_tracker.mapper.AlertMapper;
import com.gevernova.crypto_wallet_tracker.repository.CoinPriceRepository;
import com.gevernova.crypto_wallet_tracker.repository.PriceAlertRepository;
import com.gevernova.crypto_wallet_tracker.util.jms.AlertEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlertServiceTest {

    @Mock
    private PriceAlertRepository alertRepo;

    @Mock
    private CoinPriceRepository coinPriceRepo;

    @Mock
    private AlertMapper alertMapper;

    @Mock
    private AlertEmailService emailService;

    @InjectMocks
    private AlertService alertService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Test createAlert ---
    @Test
    void testCreateAlert() {
        AlertRequestDTO dto = new AlertRequestDTO("BTC", 30000.0, "ABOVE");
        PriceAlert entity = PriceAlert.builder().coin("BTC").targetPrice(30000.0).condition("ABOVE").email("test@example.com").build();
        PriceAlert savedEntity = PriceAlert.builder().id(1L).coin("BTC").targetPrice(30000.0).condition("ABOVE").email("test@example.com").build();
        AlertResponseDTO responseDTO = AlertResponseDTO.builder().id(1L).coin("BTC").targetPrice(30000.0).condition("ABOVE").triggered(false).build();

        when(alertMapper.toEntity(dto, "test@example.com")).thenReturn(entity);
        when(alertRepo.save(entity)).thenReturn(savedEntity);
        when(alertMapper.toDTO(savedEntity)).thenReturn(responseDTO);

        AlertResponseDTO result = alertService.createAlert(dto, "test@example.com");

        assertNotNull(result);
        assertEquals("BTC", result.getCoin());
        verify(alertRepo).save(entity);
    }

    // --- Test getAlerts ---
    @Test
    void testGetAlerts() {
        String email = "user@example.com";

        PriceAlert alert1 = PriceAlert.builder().id(1L).coin("BTC").email(email).build();
        PriceAlert alert2 = PriceAlert.builder().id(2L).coin("ETH").email(email).build();

        AlertResponseDTO dto1 = AlertResponseDTO.builder().id(1L).coin("BTC").build();
        AlertResponseDTO dto2 = AlertResponseDTO.builder().id(2L).coin("ETH").build();

        when(alertRepo.findByEmail(email)).thenReturn(Arrays.asList(alert1, alert2));
        when(alertMapper.toDTO(alert1)).thenReturn(dto1);
        when(alertMapper.toDTO(alert2)).thenReturn(dto2);

        List<AlertResponseDTO> alerts = alertService.getAlerts(email);

        assertEquals(2, alerts.size());
        verify(alertRepo).findByEmail(email);
    }

    // --- Test evaluateAlerts: Positive (trigger condition met) ---
    @Test
    void testEvaluateAlerts_ConditionMet_Above() {
        PriceAlert alert = PriceAlert.builder()
                .id(1L)
                .coin("BTC")
                .condition("ABOVE")
                .targetPrice(30000.0)
                .email("test@example.com")
                .triggered(false)
                .build();

        CoinPrice coinPrice = CoinPrice.builder()
                .coinSymbol("btc")
                .currentPrice(31000.0)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(alertRepo.findByTriggeredFalse()).thenReturn(List.of(alert));
        when(coinPriceRepo.findByCoinSymbol("btc")).thenReturn(Optional.of(coinPrice));

        alertService.evaluateAlerts();

        assertTrue(alert.isTriggered());
        verify(alertRepo).save(alert);
        verify(emailService).sendAlertEmail("test@example.com", "btc", 30000.0, 31000.0);
    }

    // --- Test evaluateAlerts: Negative (condition not met) ---
    @Test
    void testEvaluateAlerts_ConditionNotMet_Below() {
        PriceAlert alert = PriceAlert.builder()
                .coin("ETH")
                .condition("BELOW")
                .targetPrice(1500.0)
                .email("eth@example.com")
                .triggered(false)
                .build();

        CoinPrice coinPrice = CoinPrice.builder()
                .coinSymbol("eth")
                .currentPrice(1700.0)
                .build();

        when(alertRepo.findByTriggeredFalse()).thenReturn(List.of(alert));
        when(coinPriceRepo.findByCoinSymbol("eth")).thenReturn(Optional.of(coinPrice));

        alertService.evaluateAlerts();

        assertFalse(alert.isTriggered());
        verify(alertRepo, never()).save(alert);
        verify(emailService, never()).sendAlertEmail(any(), any(), anyDouble(), anyDouble());
    }

    // --- Test evaluateAlerts: No market price available ---
    @Test
    void testEvaluateAlerts_MarketPriceMissing() {
        PriceAlert alert = PriceAlert.builder()
                .coin("DOGE")
                .condition("ABOVE")
                .targetPrice(0.1)
                .email("user@example.com")
                .triggered(false)
                .build();

        when(alertRepo.findByTriggeredFalse()).thenReturn(List.of(alert));
        when(coinPriceRepo.findByCoinSymbol("doge")).thenReturn(Optional.empty());

        alertService.evaluateAlerts();

        assertFalse(alert.isTriggered());
        verify(alertRepo, never()).save(alert);
        verify(emailService, never()).sendAlertEmail(any(), any(), anyDouble(), anyDouble());
    }

}
