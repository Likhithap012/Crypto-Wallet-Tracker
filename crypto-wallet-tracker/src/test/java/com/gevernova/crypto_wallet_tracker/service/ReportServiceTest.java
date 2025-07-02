package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.response.SummaryResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.WalletEntry;
import com.gevernova.crypto_wallet_tracker.repository.WalletRepository;
import com.itextpdf.text.DocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private SummaryService summaryService;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGeneratePdfReport() throws IOException, DocumentException {
        String email = "test@example.com";

        // Sample data
        WalletEntry entry = WalletEntry.builder()
                .coin("bitcoin")
                .units(2.5)
                .buyPrice(1000000.0)
                .build();

        SummaryResponseDTO summary = SummaryResponseDTO.builder()
                .totalInvestment(2500000.0)
                .currentValue(2700000.0)
                .netGainLoss(200000.0)
                .netGainLossPercent(8.0)
                .build();

        when(walletRepository.findByUserEmail(email)).thenReturn(List.of(entry));
        when(summaryService.getSummaryForUser(email)).thenReturn(summary);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        reportService.generatePdfReport(out, email);

        byte[] pdfBytes = out.toByteArray();
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void testGenerateExcelReport() throws IOException {
        String email = "test@example.com";

        WalletEntry entry = WalletEntry.builder()
                .coin("ethereum")
                .units(1.0)
                .buyPrice(150000.0)
                .build();

        SummaryResponseDTO summary = SummaryResponseDTO.builder()
                .totalInvestment(150000.0)
                .currentValue(160000.0)
                .netGainLoss(10000.0)
                .netGainLossPercent(6.67)
                .build();

        when(walletRepository.findByUserEmail(email)).thenReturn(List.of(entry));
        when(summaryService.getSummaryForUser(email)).thenReturn(summary);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        reportService.generateExcelReport(out, email);

        byte[] excelBytes = out.toByteArray();
        assertTrue(excelBytes.length > 0);
    }
}
