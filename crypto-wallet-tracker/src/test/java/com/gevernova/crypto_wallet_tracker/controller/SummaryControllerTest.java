package com.gevernova.crypto_wallet_tracker.controller;

import com.gevernova.crypto_wallet_tracker.dto.response.SummaryResponseDTO;
import com.gevernova.crypto_wallet_tracker.exceptions.GlobalExceptionHandler;
import com.gevernova.crypto_wallet_tracker.service.SummaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SummaryControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private SummaryController summaryController;

    @Mock
    private SummaryService summaryService;

    @Mock
    private Authentication authentication;

    private SummaryResponseDTO mockResponse;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(summaryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockResponse = SummaryResponseDTO.builder()
                .totalInvestment(1000.0)
                .currentValue(1200.0)
                .netGainLoss(200.0)
                .netGainLossPercent(20.0)
                .build();
    }

    //  Positive test case
    @Test
    void testGetSummary_Success() throws Exception {
        String userEmail = "user@example.com";
        when(authentication.getName()).thenReturn(userEmail);
        when(summaryService.getSummaryForUser(userEmail)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/summary")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInvestment").value(1000.0))
                .andExpect(jsonPath("$.currentValue").value(1200.0))
                .andExpect(jsonPath("$.netGainLoss").value(200.0))
                .andExpect(jsonPath("$.netGainLossPercent").value(20.0));

        verify(summaryService, times(1)).getSummaryForUser(userEmail);
    }

    //  Negative test case
    @Test
    void testGetSummary_ServiceFailure() throws Exception {
        String userEmail = "user@example.com";
        when(authentication.getName()).thenReturn(userEmail);
        when(summaryService.getSummaryForUser(userEmail))
                .thenThrow(new RuntimeException("Internal service error"));

        mockMvc.perform(get("/api/summary")
                        .principal(authentication))
                .andExpect(status().is5xxServerError());

        verify(summaryService, times(1)).getSummaryForUser(userEmail);
    }
}
