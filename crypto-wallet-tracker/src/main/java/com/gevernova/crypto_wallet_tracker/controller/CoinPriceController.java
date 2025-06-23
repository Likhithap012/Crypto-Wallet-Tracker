package com.gevernova.crypto_wallet_tracker.controller;

import com.gevernova.crypto_wallet_tracker.dto.response.CoinPriceResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;
import com.gevernova.crypto_wallet_tracker.mapper.CoinPriceMapper;
import com.gevernova.crypto_wallet_tracker.repository.CoinPriceRepository;
import com.gevernova.crypto_wallet_tracker.service.CoinPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/price")
@RequiredArgsConstructor
public class CoinPriceController {

    private final CoinPriceRepository coinPriceRepository;
    private final CoinPriceMapper mapper;
    private final CoinPriceService coinPriceService;

    @GetMapping("/{symbol}")
    public ResponseEntity<CoinPriceResponseDTO> getPrice(@PathVariable String symbol) {
        Optional<CoinPrice> coin = coinPriceRepository.findByCoinSymbol(symbol.toLowerCase());
        return coin.map(value -> ResponseEntity.ok(mapper.makeCoinPriceResponseDTO(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/fetch-latest")
    public ResponseEntity<String> fetchLatest() {
        coinPriceService.fetchAndSave();  // manually trigger fetch
        return ResponseEntity.ok("Manually fetched latest coin prices.");
    }

    @GetMapping("/fetch-all")
    public ResponseEntity<List<CoinPriceResponseDTO>> getAllPrices() {
        List<CoinPrice> prices = coinPriceService.getAllCoinPrices();
        List<CoinPriceResponseDTO> response = prices.stream()
                .map(mapper::makeCoinPriceResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }
}
