package com.gevernova.crypto_wallet_tracker.mapper;

import com.gevernova.crypto_wallet_tracker.dto.response.CoinPriceResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CoinPriceMapperTest {

    private CoinPriceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CoinPriceMapper();
    }

    // Positive Test: Valid coin price
    @Test
    void testMakeCoinPriceResponseDTO_ValidInput() {
        CoinPrice coinPrice = CoinPrice.builder()
                .coinSymbol("BTC")
                .currentPrice(31000.5)
                .lastUpdated(LocalDateTime.now())
                .build();

        CoinPriceResponseDTO dto = mapper.makeCoinPriceResponseDTO(coinPrice);

        assertEquals("BTC", dto.getCoinSymbol());
        assertEquals(31000.5, dto.getCurrentPrice());
        assertEquals(coinPrice.getLastUpdated(), dto.getLastUpdated());
    }

    // Negative Test: Null coinPrice input
    @Test
    void testMakeCoinPriceResponseDTO_NullInput() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            mapper.makeCoinPriceResponseDTO(null);
        });

        assertTrue(exception.getMessage().contains("coinPrice"));
    }

    // Negative Test: Null fields in CoinPrice (symbol)
    @Test
    void testMakeCoinPriceResponseDTO_NullSymbol() {
        CoinPrice coinPrice = CoinPrice.builder()
                .coinSymbol(null)
                .currentPrice(1000.0)
                .lastUpdated(LocalDateTime.now())
                .build();

        CoinPriceResponseDTO dto = mapper.makeCoinPriceResponseDTO(coinPrice);

        assertNull(dto.getCoinSymbol());
        assertEquals(1000.0, dto.getCurrentPrice());
    }

    // Negative Test: Null fields in CoinPrice (price)
    @Test
    void testMakeCoinPriceResponseDTO_NullPrice() {
        CoinPrice coinPrice = CoinPrice.builder()
                .coinSymbol("ETH")
                .currentPrice(null)
                .lastUpdated(LocalDateTime.now())
                .build();

        CoinPriceResponseDTO dto = mapper.makeCoinPriceResponseDTO(coinPrice);

        assertEquals("ETH", dto.getCoinSymbol());
        assertNull(dto.getCurrentPrice());
    }

    // Negative Test: Null fields in CoinPrice (lastUpdated)
    @Test
    void testMakeCoinPriceResponseDTO_NullLastUpdated() {
        CoinPrice coinPrice = CoinPrice.builder()
                .coinSymbol("XRP")
                .currentPrice(0.5)
                .lastUpdated(null)
                .build();

        CoinPriceResponseDTO dto = mapper.makeCoinPriceResponseDTO(coinPrice);

        assertEquals("XRP", dto.getCoinSymbol());
        assertEquals(0.5, dto.getCurrentPrice());
        assertNull(dto.getLastUpdated());
    }

    // Edge Case Test: Empty symbol
    @Test
    void testMakeCoinPriceResponseDTO_EmptySymbol() {
        CoinPrice coinPrice = CoinPrice.builder()
                .coinSymbol("")
                .currentPrice(150.0)
                .lastUpdated(LocalDateTime.now())
                .build();

        CoinPriceResponseDTO dto = mapper.makeCoinPriceResponseDTO(coinPrice);

        assertEquals("", dto.getCoinSymbol());
        assertEquals(150.0, dto.getCurrentPrice());
    }

    // Edge Case Test: Very large price value
    @Test
    void testMakeCoinPriceResponseDTO_LargePrice() {
        CoinPrice coinPrice = CoinPrice.builder()
                .coinSymbol("ADA")
                .currentPrice(Double.MAX_VALUE)
                .lastUpdated(LocalDateTime.now())
                .build();

        CoinPriceResponseDTO dto = mapper.makeCoinPriceResponseDTO(coinPrice);

        assertEquals("ADA", dto.getCoinSymbol());
        assertEquals(Double.MAX_VALUE, dto.getCurrentPrice());
    }

    // Edge Case Test: Zero price
    @Test
    void testMakeCoinPriceResponseDTO_ZeroPrice() {
        CoinPrice coinPrice = CoinPrice.builder()
                .coinSymbol("DOGE")
                .currentPrice(0.0)
                .lastUpdated(LocalDateTime.now())
                .build();

        CoinPriceResponseDTO dto = mapper.makeCoinPriceResponseDTO(coinPrice);

        assertEquals("DOGE", dto.getCoinSymbol());
        assertEquals(0.0, dto.getCurrentPrice());
    }
}
