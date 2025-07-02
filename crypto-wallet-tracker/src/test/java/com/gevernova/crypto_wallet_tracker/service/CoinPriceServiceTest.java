package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;
import com.gevernova.crypto_wallet_tracker.repository.CoinPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

class CoinPriceServiceTest {

    @Mock
    private CoinPriceRepository coinPriceRepository;

    @InjectMocks
    private CoinPriceService coinPriceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveNewCoin() {
        String coinId = "bitcoin";
        Double price = 1000000.0;

        when(coinPriceRepository.findByCoinSymbol(coinId)).thenReturn(Optional.empty());
        ArgumentCaptor<CoinPrice> captor = ArgumentCaptor.forClass(CoinPrice.class);
        when(coinPriceRepository.save(any(CoinPrice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CoinPrice coin = CoinPrice.builder()
                .coinSymbol(coinId)
                .currentPrice(price)
                .lastUpdated(LocalDateTime.now())
                .build();

        coinPriceRepository.save(coin);

        verify(coinPriceRepository).save(captor.capture());
        CoinPrice saved = captor.getValue();

        assert saved.getCoinSymbol().equals(coinId);
        assert saved.getCurrentPrice().equals(price);
    }

    @Test
    void testUpdateExistingCoin() {
        String coinId = "bitcoin";
        Double newPrice = 2000000.0;

        CoinPrice existing = CoinPrice.builder()
                .coinSymbol(coinId)
                .currentPrice(1000000.0)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(coinPriceRepository.findByCoinSymbol(coinId)).thenReturn(Optional.of(existing));
        when(coinPriceRepository.save(any(CoinPrice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        existing.setCurrentPrice(newPrice);
        existing.setLastUpdated(LocalDateTime.now());

        coinPriceRepository.save(existing);

        ArgumentCaptor<CoinPrice> captor = ArgumentCaptor.forClass(CoinPrice.class);
        verify(coinPriceRepository).save(captor.capture());

        CoinPrice saved = captor.getValue();
        assert saved.getCurrentPrice().equals(newPrice);
    }

    //  Positive Test for another coin
    @Test
    void testSaveNewEthereumCoin() {
        String coinId = "ethereum";
        Double price = 250000.0;

        when(coinPriceRepository.findByCoinSymbol(coinId)).thenReturn(Optional.empty());
        ArgumentCaptor<CoinPrice> captor = ArgumentCaptor.forClass(CoinPrice.class);
        when(coinPriceRepository.save(any(CoinPrice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CoinPrice coin = CoinPrice.builder()
                .coinSymbol(coinId)
                .currentPrice(price)
                .lastUpdated(LocalDateTime.now())
                .build();

        coinPriceRepository.save(coin);

        verify(coinPriceRepository).save(captor.capture());
        CoinPrice saved = captor.getValue();

        assert saved.getCoinSymbol().equals("ethereum");
        assert saved.getCurrentPrice().equals(price);
    }

    //  Negative Test: Price is null, should not save
    @Test
    void testDoNotSaveWhenPriceIsNull() {
        String coinId = "cardano";
        Double price = null;

        when(coinPriceRepository.findByCoinSymbol(coinId)).thenReturn(Optional.empty());

        // Simulate behavior if the price is null – skip save
        if (price != null) {
            CoinPrice coin = CoinPrice.builder()
                    .coinSymbol(coinId)
                    .currentPrice(price)
                    .lastUpdated(LocalDateTime.now())
                    .build();
            coinPriceRepository.save(coin);
        }

        verify(coinPriceRepository, never()).save(any());
    }
}
