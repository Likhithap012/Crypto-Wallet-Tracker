package com.gevernova.crypto_wallet_tracker.controller;

import com.gevernova.crypto_wallet_tracker.service.ReportService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/export")
    public void exportReport(@RequestParam String format,
                             HttpServletResponse response,
                             Authentication authentication) throws IOException, DocumentException {
        String email = authentication.getName();
        if (format.equalsIgnoreCase("excel")) {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=report.xlsx");

            reportService.generateExcelReport(response.getOutputStream(), email);
            response.flushBuffer();
        } else if (format.equalsIgnoreCase("pdf")) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=report.pdf");

            reportService.generatePdfReport(response.getOutputStream(), email);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported format: " + format);
        }
    }

}
