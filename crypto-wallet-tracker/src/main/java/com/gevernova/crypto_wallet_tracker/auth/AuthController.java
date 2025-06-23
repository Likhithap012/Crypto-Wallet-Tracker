package com.gevernova.crypto_wallet_tracker.auth;

import com.gevernova.crypto_wallet_tracker.dto.request.*;
import com.gevernova.crypto_wallet_tracker.dto.response.AuthResponseDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.OtpLoginResponseDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.ResetPasswordResponseDTO;
import com.gevernova.crypto_wallet_tracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String email) {
        String message = authService.verifyEmail(email);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/register/verified")
    public ResponseEntity<String> checkVerified(@RequestParam String email) {
        String message = authService.checkVerificationStatus(email);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody OtpLoginRequestDTO login) {
        if ("password".equalsIgnoreCase(login.getMethod())) {
            return ResponseEntity.ok(authService.loginWithPassword(
                    new LoginRequestDTO(login.getEmail(), login.getPassword())));
        } else if ("otp".equalsIgnoreCase(login.getMethod())) {
            return ResponseEntity.ok(authService.loginWithOtp(login.getEmail(), login.getOtp()));
        } else {
            throw new IllegalArgumentException("Invalid login method. Use 'password' or 'otp'.");
        }
    }
    @PostMapping ("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequestDTO request){
        return ResponseEntity.ok(authService.forgotPassword(request.getEmail()));
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword,
                                Model model) {
        String email = authService.resetPassword(token, newPassword);
        model.addAttribute("email", email);
        return "success";  // This will render success.html
    }


    @GetMapping("/reset-password-form")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password"; // DO NOT return literal HTML content
    }
}
