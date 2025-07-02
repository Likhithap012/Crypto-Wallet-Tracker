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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private UserRepository userRepo;
    @Mock private PasswordEncoder encoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authManager;
    @Mock private EmailService emailService;
    @Mock private Authentication authentication;

    @InjectMocks private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_shouldSucceed_whenEmailIsNew() throws MessagingException {
        RegisterRequestDTO req = RegisterRequestDTO.builder()
                .name("John")
                .email("john@example.com")
                .password("password123")
                .role("USER")
                .build();

        when(userRepo.existsByEmail(req.getEmail())).thenReturn(false);
        when(encoder.encode(req.getPassword())).thenReturn("encoded");
        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        String result = authService.register(req);

        assertTrue(result.contains("User registered successfully"));
        verify(emailService).sendEmail(eq(req.getEmail()), any(), contains("Verify Email"));
    }

    @Test
    void register_shouldFail_whenEmailAlreadyExists() {
        RegisterRequestDTO req = RegisterRequestDTO.builder()
                .name("John")
                .email("john@example.com")
                .password("password123")
                .role("USER")
                .build();

        when(userRepo.existsByEmail(req.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(req));
    }

    @Test
    void verifyEmail_shouldSucceed_whenUserExistsAndNotVerified() {
        User user = User.builder().email("john@example.com").verified(false).build();
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        String result = authService.verifyEmail(user.getEmail());

        assertEquals("Email verified successfully.", result);
        assertTrue(user.isVerified());
    }

    @Test
    void verifyEmail_shouldReturnAlreadyVerified_whenAlreadyVerified() {
        User user = User.builder().email("john@example.com").verified(true).build();
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        String result = authService.verifyEmail(user.getEmail());

        assertEquals("Already verified.", result);
    }

    @Test
    void checkVerificationStatus_shouldResendOtp_whenVerified() throws MessagingException {
        User user = User.builder().email("john@example.com").verified(true).otp("123456").build();
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        String result = authService.checkVerificationStatus(user.getEmail());

        assertEquals("Email verified. OTP has been resent.", result);
    }

    @Test
    void loginWithPassword_shouldSucceed_whenCredentialsAreCorrect() {
        LoginRequestDTO login = LoginRequestDTO.builder()
                .email("john@example.com")
                .password("password123")
                .build();

        User user = User.builder().email(login.getEmail()).password("encoded").name("John").role(Role.USER).verified(true).build();

        when(userRepo.findByEmail(login.getEmail())).thenReturn(Optional.of(user));
        when(authManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(user.getEmail(), user.getRole().name())).thenReturn("token");

        AuthResponseDTO response = authService.loginWithPassword(login);

        assertEquals("token", response.getToken());
    }

    @Test
    void loginWithOtp_shouldSucceed_whenOtpIsCorrect() {
        User user = User.builder().email("john@example.com").otp("123456").name("John").role(Role.USER).verified(true).build();
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user.getEmail(), user.getRole().name())).thenReturn("token");

        AuthResponseDTO result = authService.loginWithOtp(user.getEmail(), "123456");

        assertEquals("token", result.getToken());
    }

    @Test
    void forgotPassword_shouldGenerateTokenAndSendEmail() throws MessagingException {
        User user = User.builder().email("john@example.com").build();
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        String result = authService.forgotPassword(user.getEmail());

        assertTrue(result.contains("Password reset link sent"));
        assertNotNull(user.getResetToken());
    }

    @Test
    void resetPassword_shouldUpdatePassword_whenTokenValid() {
        String token = UUID.randomUUID().toString();
        User user = User.builder().resetToken(token).resetTokenExpiry(LocalDateTime.now().plusMinutes(5)).email("john@example.com").build();

        when(userRepo.findByResetToken(token)).thenReturn(Optional.of(user));

        String email = authService.resetPassword(token, "newPassword");

        assertEquals(user.getEmail(), email);
        assertNull(user.getResetToken());
    }

    @Test
    void resetPassword_shouldThrowException_whenTokenExpired() {
        String token = UUID.randomUUID().toString();
        User user = User.builder().resetToken(token).resetTokenExpiry(LocalDateTime.now().minusMinutes(1)).build();

        when(userRepo.findByResetToken(token)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authService.resetPassword(token, "newPass"));
    }
}