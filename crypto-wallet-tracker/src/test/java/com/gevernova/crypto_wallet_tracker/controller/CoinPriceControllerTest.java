package com.gevernova.crypto_wallet_tracker.controller;

import com.gevernova.crypto_wallet_tracker.dto.response.CoinPriceResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;
import com.gevernova.crypto_wallet_tracker.mapper.CoinPriceMapper;
import com.gevernova.crypto_wallet_tracker.repository.CoinPriceRepository;
import com.gevernova.crypto_wallet_tracker.service.CoinPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoinPriceControllerTest {

    private CoinPriceRepository coinPriceRepository;
    private CoinPriceMapper mapper;
    private CoinPriceService coinPriceService;
    private CoinPriceController controller;

    @BeforeEach
    void setUp() {
        coinPriceRepository = mock(CoinPriceRepository.class);
        mapper = mock(CoinPriceMapper.class);
        coinPriceService = mock(CoinPriceService.class);
        controller = new CoinPriceController(coinPriceRepository, mapper, coinPriceService);
    }

    // Test: GET /api/price/{symbol} - valid coin
    @Test
    void testGetPrice_Found() {
        String symbol = "btc";
        CoinPrice coin = new CoinPrice();
        CoinPriceResponseDTO responseDTO = new CoinPriceResponseDTO();

        when(coinPriceRepository.findByCoinSymbol(symbol)).thenReturn(Optional.of(coin));
        when(mapper.makeCoinPriceResponseDTO(coin)).thenReturn(responseDTO);

        ResponseEntity<CoinPriceResponseDTO> response = controller.getPrice(symbol);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    // Test: GET /api/price/{symbol} - coin not found
    @Test
    void testGetPrice_NotFound() {
        String symbol = "xyz";

        when(coinPriceRepository.findByCoinSymbol(symbol)).thenReturn(Optional.empty());

        ResponseEntity<CoinPriceResponseDTO> response = controller.getPrice(symbol);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    // Test: POST /api/price/fetch-latest
    @Test
    void testFetchLatest() {
        // No return value, just verify method was called
        doNothing().when(coinPriceService).fetchAndSave();

        ResponseEntity<String> response = controller.fetchLatest();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Manually fetched latest coin prices.", response.getBody());
        verify(coinPriceService).fetchAndSave();
    }

    // Test: GET /api/price/fetch-all - with prices
    @Test
    void testGetAllPrices_WithData() {
        CoinPrice coin1 = new CoinPrice();
        CoinPrice coin2 = new CoinPrice();
        List<CoinPrice> coins = List.of(coin1, coin2);

        CoinPriceResponseDTO dto1 = new CoinPriceResponseDTO();
        CoinPriceResponseDTO dto2 = new CoinPriceResponseDTO();

        when(coinPriceService.getAllCoinPrices()).thenReturn(coins);
        when(mapper.makeCoinPriceResponseDTO(coin1)).thenReturn(dto1);
        when(mapper.makeCoinPriceResponseDTO(coin2)).thenReturn(dto2);

        ResponseEntity<List<CoinPriceResponseDTO>> response = controller.getAllPrices();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(List.of(dto1, dto2), response.getBody());
    }

    // Test: GET /api/price/fetch-all - no prices
    @Test
    void testGetAllPrices_EmptyList() {
        when(coinPriceService.getAllCoinPrices()).thenReturn(Collections.emptyList());

        ResponseEntity<List<CoinPriceResponseDTO>> response = controller.getAllPrices();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }
}
