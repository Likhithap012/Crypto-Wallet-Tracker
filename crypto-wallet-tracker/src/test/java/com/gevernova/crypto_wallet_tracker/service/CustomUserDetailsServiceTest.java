package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.entity.Role;
import com.gevernova.crypto_wallet_tracker.entity.User;
import com.gevernova.crypto_wallet_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Positive Test Case: Valid email with USER role
    @Test
    void shouldLoadUserDetails_WhenEmailExistsWithUserRole() {
        User user = User.builder()
                .email("user@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("user@example.com");

        assertNotNull(userDetails);
        assertEquals("user@example.com", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    // Positive Test Case: Valid email with ADMIN role
    @Test
    void shouldLoadUserDetails_WhenEmailExistsWithAdminRole() {
        User user = User.builder()
                .email("admin@example.com")
                .password("adminpass")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin@example.com");

        assertNotNull(userDetails);
        assertEquals("admin@example.com", userDetails.getUsername());
        assertEquals("adminpass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    // Negative Test Case: Email not found
    @Test
    void shouldThrowException_WhenEmailNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("notfound@example.com"));
    }

    // Negative Test Case: Email is null
    @Test
    void shouldThrowException_WhenEmailIsNull() {
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(null));
    }

    // Negative Test Case: Email is empty
    @Test
    void shouldThrowException_WhenEmailIsEmpty() {
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(""));
    }
}
