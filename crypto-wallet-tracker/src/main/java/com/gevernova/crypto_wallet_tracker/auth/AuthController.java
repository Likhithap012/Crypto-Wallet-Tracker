package com.gevernova.crypto_wallet_tracker.auth;

import com.gevernova.crypto_wallet_tracker.dto.request.LoginRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.request.RegisterRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.AuthResponseDTO;
import com.gevernova.crypto_wallet_tracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
