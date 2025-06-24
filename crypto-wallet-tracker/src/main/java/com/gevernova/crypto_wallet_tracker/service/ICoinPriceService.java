package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;

import java.util.List;

public interface ICoinPriceService{
    void fetchAndSave();
    List<CoinPrice> getAllCoinPrices();
}