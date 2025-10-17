package com.library;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportGenerator {
    
    public static void generatePdfReport(List<PropertyApp.Property> properties, String filePath) {
        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Add title
            document.add(new Paragraph("Property Management Report")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold());

            // Add date
            document.add(new Paragraph("Generated on: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(12));

            // Create table
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 3, 2, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Add headers
            String[] headers = {"Property ID", "Owner", "Address", "Rent", "Mortgage", "Status", "Profit"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header));
                cell.setBold();
                table.addHeaderCell(cell);
            }

            // Add data
            double totalRent = 0;
            double totalMortgage = 0;
            double totalProfit = 0;

            for (PropertyApp.Property property : properties) {
                table.addCell(new Cell().add(new Paragraph(property.getPropertyId())));
                table.addCell(new Cell().add(new Paragraph(property.getOwnerName())));
                table.addCell(new Cell().add(new Paragraph(property.getAddress())));
                table.addCell(new Cell().add(new Paragraph(String.format("£%.2f", property.getMonthlyRent()))));
                table.addCell(new Cell().add(new Paragraph(String.format("£%.2f", property.getMonthlyMortgage()))));
                table.addCell(new Cell().add(new Paragraph(property.getStatus())));
                table.addCell(new Cell().add(new Paragraph(String.format("£%.2f", property.getMonthlyProfit()))));

                totalRent += property.getMonthlyRent();
                totalMortgage += property.getMonthlyMortgage();
                totalProfit += property.getMonthlyProfit();
            }

            document.add(table);

            // Add summary
            document.add(new Paragraph("\nSummary")
                .setBold()
                .setFontSize(14));
            document.add(new Paragraph(String.format("Total Monthly Rent: £%.2f", totalRent)));
            document.add(new Paragraph(String.format("Total Monthly Mortgage: £%.2f", totalMortgage)));
            document.add(new Paragraph(String.format("Total Monthly Profit: £%.2f", totalProfit)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportToExcel(List<PropertyApp.Property> properties, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Properties");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Property ID", "Owner", "Address", "Monthly Rent", "Monthly Mortgage", 
                              "Status", "Monthly Profit", "Bedrooms", "Living Rooms", "Kitchens", 
                              "Bathrooms", "House Type", "Description"};
            
            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (PropertyApp.Property property : properties) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(property.getPropertyId());
                row.createCell(1).setCellValue(property.getOwnerName());
                row.createCell(2).setCellValue(property.getAddress());
                row.createCell(3).setCellValue(property.getMonthlyRent());
                row.createCell(4).setCellValue(property.getMonthlyMortgage());
                row.createCell(5).setCellValue(property.getStatus());
                row.createCell(6).setCellValue(property.getMonthlyProfit());
                row.createCell(7).setCellValue(property.getBedrooms());
                row.createCell(8).setCellValue(property.getLivingRooms());
                row.createCell(9).setCellValue(property.getKitchens());
                row.createCell(10).setCellValue(property.getBathrooms());
                row.createCell(11).setCellValue(property.getHouseType());
                row.createCell(12).setCellValue(property.getDescription());
            }

            // Autosize columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}