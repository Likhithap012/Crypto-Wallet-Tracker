package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.request.LoginRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.request.RegisterRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.AuthResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.Role;
import com.gevernova.crypto_wallet_tracker.entity.User;
import com.gevernova.crypto_wallet_tracker.repository.UserRepository;
import com.gevernova.crypto_wallet_tracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthResponseDTO register(RegisterRequestDTO req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role role;
        try {
            role = Role.valueOf(req.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Allowed roles: USER, ADMIN");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .role(role)
                .build();

        userRepo.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponseDTO(token, user.getName(), user.getRole().name());
    }

    public AuthResponseDTO login(LoginRequestDTO req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepo.findByEmail(req.getEmail()).orElseThrow(() ->
                new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponseDTO(token, user.getName(), user.getRole().name());
    }

}
