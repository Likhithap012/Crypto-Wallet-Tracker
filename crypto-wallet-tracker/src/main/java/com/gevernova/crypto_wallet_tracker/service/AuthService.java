package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.request.LoginRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.request.RegisterRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.AuthResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.Role;
import com.gevernova.crypto_wallet_tracker.entity.User;
import com.gevernova.crypto_wallet_tracker.repository.UserRepository;
import com.gevernova.crypto_wallet_tracker.security.JwtUtil;
import com.gevernova.crypto_wallet_tracker.util.jms.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceInterface {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final EmailService emailService;

    public String register(RegisterRequestDTO req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role role = Role.valueOf(req.getRole().toUpperCase());
        String otp = generateOtp();

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .role(role)
                .verified(false)
                .otp(otp)
                .build();

        userRepo.save(user);

        String verifyLink = "http://localhost:8080/api/auth/verify?email=" + req.getEmail();
        String htmlBody = "<h3>Welcome to Crypto Wallet Tracker</h3>"
                + "<p>Click below to verify your email:</p>"
                + "<a href=\"" + verifyLink + "\">Verify Email</a>"
                + "<br><br>"
                + "<p>Your OTP for login is: <strong>" + otp + "</strong></p>";

        try {
            emailService.sendEmail(req.getEmail(), "Verify your email & OTP", htmlBody);
        } catch (MessagingException e) {
            e.printStackTrace();
            return "User registered but failed to send email. Please contact support.";
        }

        return "User registered successfully. Please check your email to verify and retrieve OTP.";
    }

    public String verifyEmail(String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        User user = optionalUser.get();
        if (user.isVerified()) {
            return "Already verified.";
        }

        user.setVerified(true);
        userRepo.save(user);

        return "Email verified successfully.";
    }

    public String checkVerificationStatus(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        if (user.isEmpty()) {
            return "User not found.";
        }

        if (!user.get().isVerified()) {
            return "Email not verified yet.";
        }

        // Instead of exposing OTP here, re-send email
        String htmlBody = "<h3>Your OTP for login</h3>"
                + "<p>OTP: <strong>" + user.get().getOtp() + "</strong></p>";

        try {
            emailService.sendEmail(email, "Your OTP for Crypto Wallet", htmlBody);
            return "Email verified. OTP has been resent.";
        } catch (MessagingException e) {
            return "Email verified, but failed to resend OTP.";
        }
    }

    public AuthResponseDTO loginWithPassword(LoginRequestDTO req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your email first.");
        }


        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponseDTO(token, user.getName(), user.getRole().name());
    }

    public AuthResponseDTO loginWithOtp(String email, String otp) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your email first.");
        }

        if (!user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponseDTO(token, user.getName(), user.getRole().name());
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public String forgotPassword(String email){
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepo.save(user);

        String link = "http://localhost:8080/api/auth/reset-password-form?token=" + token;
        String content = """
                <h3>Password Reset Requested</h3>
                <p>We received a request to reset your password.</p>
                <p><a href="%s">Click here to reset your password</a></p>
                <p>This link will expire in 15 minutes.</p>
                """.formatted(link);
        try{
            emailService.sendEmail(user.getEmail(), "Password Reset", content);
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Failed to send email";
        }
        return "Password reset link sent to your email.";
    }

    public String resetPassword(String token, String newPassword){
        User user = userRepo.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }
        user.setPassword(encoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepo.save(user);

        return user.getEmail();
    }
}