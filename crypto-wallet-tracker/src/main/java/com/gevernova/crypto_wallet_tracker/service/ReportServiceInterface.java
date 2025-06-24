package com.gevernova.crypto_wallet_tracker.service;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.OutputStream;

public interface ReportServiceInterface {
    void generatePdfReport(OutputStream out, String userEmail) throws IOException, DocumentException;
    void generateExcelReport(OutputStream out, String userEmail) throws IOException;
}
