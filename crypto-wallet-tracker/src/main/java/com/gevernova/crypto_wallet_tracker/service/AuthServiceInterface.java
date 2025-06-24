package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.request.LoginRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.request.RegisterRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.AuthResponseDTO;

public interface AuthServiceInterface {

    String register(RegisterRequestDTO req);

    String verifyEmail(String email);

    String checkVerificationStatus(String email);

    AuthResponseDTO loginWithPassword(LoginRequestDTO req);

    AuthResponseDTO loginWithOtp(String email, String otp);

    String forgotPassword(String email);

    String resetPassword(String token, String newPassword);
}