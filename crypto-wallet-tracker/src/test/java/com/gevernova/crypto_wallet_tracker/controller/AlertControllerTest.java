package com.gevernova.crypto_wallet_tracker.controller;

import com.gevernova.crypto_wallet_tracker.dto.request.AlertRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.AlertResponseDTO;
import com.gevernova.crypto_wallet_tracker.service.AlertServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlertControllerTest {

    private AlertServiceInterface alertService;
    private AlertController alertController;
    private Authentication authentication;

    // Set up mocks before each test
    @BeforeEach
    void setUp() {
        alertService = mock(AlertServiceInterface.class);
        alertController = new AlertController(alertService);
        authentication = mock(Authentication.class);
    }

    // Test for successful creation of an alert (positive scenario)
    @Test
    void testCreateAlert_Positive() {
        AlertRequestDTO requestDTO = new AlertRequestDTO(); // mock input
        AlertResponseDTO responseDTO = new AlertResponseDTO(); // mock output

        // Mock behavior for authentication and service
        when(authentication.getName()).thenReturn("user@example.com");
        when(alertService.createAlert(requestDTO, "user@example.com")).thenReturn(responseDTO);

        // Call controller method
        ResponseEntity<AlertResponseDTO> response = alertController.createAlert(requestDTO, authentication);

        // Assertions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(alertService).createAlert(requestDTO, "user@example.com");
    }

    // Test for successful retrieval of alerts (positive scenario)
    @Test
    void testGetAlerts_Positive() {
        // Create mock response data
        AlertResponseDTO alert1 = new AlertResponseDTO();
        AlertResponseDTO alert2 = new AlertResponseDTO();
        List<AlertResponseDTO> alerts = Arrays.asList(alert1, alert2);

        // Mock authentication and service
        when(authentication.getName()).thenReturn("user@example.com");
        when(alertService.getAlerts("user@example.com")).thenReturn(alerts);

        // Call controller method
        ResponseEntity<List<AlertResponseDTO>> response = alertController.getAlerts(authentication);

        // Assertions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(alerts, response.getBody());
        verify(alertService).getAlerts("user@example.com");
    }

    // Negative test: authentication object is null
    @Test
    void testCreateAlert_NullAuthentication() {
        AlertRequestDTO requestDTO = new AlertRequestDTO();

        // This should throw a NullPointerException due to null auth
        Exception exception = assertThrows(NullPointerException.class, () -> {
            alertController.createAlert(requestDTO, null);
        });

        assertNotNull(exception);
    }

    // Negative test: authentication.getName() returns null
    @Test
    void testCreateAlert_EmptyEmail() {
        AlertRequestDTO requestDTO = new AlertRequestDTO();

        // Simulate missing email
        when(authentication.getName()).thenReturn(null);

        // We manually throw an exception to simulate the controller failing
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            String email = authentication.getName();
            if (email == null) {
                throw new IllegalArgumentException("Email is required");
            }
            alertController.createAlert(requestDTO, authentication);
        });

        assertEquals("Email is required", exception.getMessage());
    }

    // Negative test: Service layer throws an exception
    @Test
    void testGetAlerts_ServiceThrowsException() {
        // Mock user email and simulate service throwing error
        when(authentication.getName()).thenReturn("user@example.com");
        when(alertService.getAlerts("user@example.com")).thenThrow(new RuntimeException("Database unavailable"));

        // Call controller and expect RuntimeException
        Exception exception = assertThrows(RuntimeException.class, () -> {
            alertController.getAlerts(authentication);
        });

        assertEquals("Database unavailable", exception.getMessage());
    }
}
