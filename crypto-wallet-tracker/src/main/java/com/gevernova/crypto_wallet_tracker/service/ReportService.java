package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.response.SummaryResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.WalletEntry;
import com.gevernova.crypto_wallet_tracker.repository.WalletRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService implements ReportServiceInterface {

    private final WalletRepository walletRepository;
    private final SummaryService summaryService; // Injected

    @Override
    public void generatePdfReport(OutputStream out, String userEmail) throws IOException, DocumentException {
        List<WalletEntry> entries = walletRepository.findByUserEmail(userEmail);
        SummaryResponseDTO summary = summaryService.getSummaryForUser(userEmail);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Paragraph title = new Paragraph("Crypto Wallet Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("User: " + userEmail));
        document.add(new Paragraph("Generated on: " + java.time.LocalDateTime.now()));
        document.add(new Paragraph(" "));

        // Summary Section
        Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        document.add(new Paragraph("Portfolio Summary", sectionTitleFont));
        document.add(new Paragraph("Total Investment: ₹" + summary.getTotalInvestment()));
        document.add(new Paragraph("Current Value: ₹" + summary.getCurrentValue()));
        document.add(new Paragraph("Net Gain/Loss: ₹" + summary.getNetGainLoss()));
        document.add(new Paragraph("Gain/Loss (%): " + summary.getNetGainLossPercent() + "%"));
        document.add(new Paragraph(" "));

        // Table Header
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        addTableHeaderCell(table, "Coin", headerFont);
        addTableHeaderCell(table, "Units", headerFont);
        addTableHeaderCell(table, "Buy Price", headerFont);

        // Table Rows
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        for (WalletEntry entry : entries) {
            addTableCell(table, entry.getCoin(), cellFont);
            addTableCell(table, String.valueOf(entry.getUnits()), cellFont);
            addTableCell(table, String.valueOf(entry.getBuyPrice()), cellFont);
        }

        document.add(table);
        document.close();
    }

    // Helper methods
    private void addTableHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }


    @Override
    public void generateExcelReport(OutputStream out, String userEmail) throws IOException {
        List<WalletEntry> entries = walletRepository.findByUserEmail(userEmail);
        SummaryResponseDTO summary = summaryService.getSummaryForUser(userEmail);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Wallet Report");

        int rowIdx = 0;

        // Summary Header
        Row summaryTitle = sheet.createRow(rowIdx++);
        summaryTitle.createCell(0).setCellValue("Portfolio Summary");

        Row total = sheet.createRow(rowIdx++);
        total.createCell(0).setCellValue("Total Investment");
        total.createCell(1).setCellValue(summary.getTotalInvestment());

        Row current = sheet.createRow(rowIdx++);
        current.createCell(0).setCellValue("Current Value");
        current.createCell(1).setCellValue(summary.getCurrentValue());

        Row gain = sheet.createRow(rowIdx++);
        gain.createCell(0).setCellValue("Net Gain/Loss");
        gain.createCell(1).setCellValue(summary.getNetGainLoss());

        Row percent = sheet.createRow(rowIdx++);
        percent.createCell(0).setCellValue("Gain/Loss (%)");
        percent.createCell(1).setCellValue(summary.getNetGainLossPercent());

        rowIdx++; // empty row

        // Table Header
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Coin");
        header.createCell(1).setCellValue("Units");
        header.createCell(2).setCellValue("Buy Price");

        for (WalletEntry entry : entries) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(entry.getCoin());
            row.createCell(1).setCellValue(entry.getUnits());
            row.createCell(2).setCellValue(entry.getBuyPrice());
        }

        workbook.write(out);
        workbook.close();
    }



}
