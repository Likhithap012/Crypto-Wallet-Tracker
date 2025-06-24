package com.gevernova.crypto_wallet_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoWalletTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoWalletTrackerApplication.class, args);
	}

}
