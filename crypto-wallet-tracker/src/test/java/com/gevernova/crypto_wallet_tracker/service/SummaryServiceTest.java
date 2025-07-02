package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.response.SummaryResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;
import com.gevernova.crypto_wallet_tracker.entity.WalletEntry;
import com.gevernova.crypto_wallet_tracker.mapper.SummaryMapper;
import com.gevernova.crypto_wallet_tracker.repository.CoinPriceRepository;
import com.gevernova.crypto_wallet_tracker.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SummaryServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CoinPriceRepository coinPriceRepository;

    @Mock
    private SummaryMapper summaryMapper;

    @InjectMocks
    private SummaryService summaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSummaryForUser_PositiveCase() {
        String email = "test@example.com";

        WalletEntry entry1 = WalletEntry.builder().coin("bitcoin").units(2.0).buyPrice(30000.0).build();
        WalletEntry entry2 = WalletEntry.builder().coin("ethereum").units(5.0).buyPrice(2000.0).build();
        List<WalletEntry> entries = Arrays.asList(entry1, entry2);

        CoinPrice btcPrice = CoinPrice.builder().coinSymbol("bitcoin").currentPrice(35000.0).build();
        CoinPrice ethPrice = CoinPrice.builder().coinSymbol("ethereum").currentPrice(2500.0).build();

        double totalInvestment = (2.0 * 30000.0) + (5.0 * 2000.0);
        double currentValue = (2.0 * 35000.0) + (5.0 * 2500.0);

        SummaryResponseDTO expectedDto = SummaryResponseDTO.builder()
                .totalInvestment(totalInvestment)
                .currentValue(currentValue)
                .netGainLoss(currentValue - totalInvestment)
                .netGainLossPercent(((currentValue - totalInvestment) / totalInvestment) * 100)
                .build();

        when(walletRepository.findByUserEmail(email)).thenReturn(entries);
        when(coinPriceRepository.findByCoinSymbol("bitcoin")).thenReturn(Optional.of(btcPrice));
        when(coinPriceRepository.findByCoinSymbol("ethereum")).thenReturn(Optional.of(ethPrice));
        when(summaryMapper.toDTO(totalInvestment, currentValue)).thenReturn(expectedDto);

        SummaryResponseDTO result = summaryService.getSummaryForUser(email);

        assertEquals(expectedDto, result);
        verify(walletRepository, times(1)).findByUserEmail(email);
    }

    @Test
    void testGetSummaryForUser_CoinPriceMissing() {
        String email = "test@example.com";

        WalletEntry entry = WalletEntry.builder().coin("unknown").units(3.0).buyPrice(100.0).build();
        List<WalletEntry> entries = List.of(entry);

        double totalInvestment = 3.0 * 100.0;
        double currentValue = 0.0;

        SummaryResponseDTO expectedDto = SummaryResponseDTO.builder()
                .totalInvestment(totalInvestment)
                .currentValue(currentValue)
                .netGainLoss(currentValue - totalInvestment)
                .netGainLossPercent(((currentValue - totalInvestment) / totalInvestment) * 100)
                .build();

        when(walletRepository.findByUserEmail(email)).thenReturn(entries);
        when(coinPriceRepository.findByCoinSymbol("unknown")).thenReturn(Optional.empty());
        when(summaryMapper.toDTO(totalInvestment, currentValue)).thenReturn(expectedDto);

        SummaryResponseDTO result = summaryService.getSummaryForUser(email);

        assertEquals(expectedDto, result);
        verify(walletRepository, times(1)).findByUserEmail(email);
        verify(coinPriceRepository, times(1)).findByCoinSymbol("unknown");
    }

    @Test
    void testGetSummaryForUser_EmptyHoldings() {
        String email = "test@example.com";
        List<WalletEntry> entries = List.of();

        double totalInvestment = 0.0;
        double currentValue = 0.0;

        SummaryResponseDTO expectedDto = SummaryResponseDTO.builder()
                .totalInvestment(totalInvestment)
                .currentValue(currentValue)
                .netGainLoss(0.0)
                .netGainLossPercent(0.0)
                .build();

        when(walletRepository.findByUserEmail(email)).thenReturn(entries);
        when(summaryMapper.toDTO(totalInvestment, currentValue)).thenReturn(expectedDto);

        SummaryResponseDTO result = summaryService.getSummaryForUser(email);

        assertEquals(expectedDto, result);
        verify(walletRepository, times(1)).findByUserEmail(email);
    }
}
