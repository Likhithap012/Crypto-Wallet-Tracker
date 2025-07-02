package com.gevernova.crypto_wallet_tracker.controller;

import com.gevernova.crypto_wallet_tracker.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.OutputStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ReportControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ReportController reportController;

    @Mock
    private ReportService reportService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
    }

    //  Positive: Export PDF
    @Test
    void testExportPdfReport_Success() throws Exception {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);

        mockMvc.perform(get("/api/report/export")
                        .param("format", "pdf")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=report.pdf"))
                .andExpect(header().string("Content-Type", "application/pdf"));

        verify(reportService, times(1)).generatePdfReport(any(OutputStream.class), eq(email));
    }

    //  Positive: Export Excel
    @Test
    void testExportExcelReport_Success() throws Exception {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);

        mockMvc.perform(get("/api/report/export")
                        .param("format", "excel")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=report.xlsx"))
                .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        verify(reportService, times(1)).generateExcelReport(any(OutputStream.class), eq(email));
    }

    //  Negative: Invalid format
    @Test
    void testExportReport_InvalidFormat() throws Exception {
        when(authentication.getName()).thenReturn("user@example.com");

        mockMvc.perform(get("/api/report/export")
                        .param("format", "csv")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(reportService, never()).generatePdfReport(any(), any());
        verify(reportService, never()).generateExcelReport(any(), any());
    }
}
