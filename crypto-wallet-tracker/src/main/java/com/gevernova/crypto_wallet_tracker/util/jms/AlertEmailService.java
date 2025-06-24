package com.gevernova.crypto_wallet_tracker.util.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertEmailService {

    private final JavaMailSender mailSender;

    public void sendAlertEmail(String email, String coin, double target, double current) {
        log.info("Sending alert email to {} for {} target={} current={}", email, coin, target, current);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Price Alert Triggered for " + coin);
        message.setText("Hi,\n\nYour price alert for " + coin + " has been triggered.\nTarget: " + target + "\nCurrent: " + current + "\n\nRegards,\nCrypto Wallet Tracker Team");

        mailSender.send(message);
    }
}