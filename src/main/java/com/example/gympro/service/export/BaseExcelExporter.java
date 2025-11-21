package com.example.gympro.service.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class cho các Excel Exporter
 * Chứa các utility methods và style creation chung
 */
public abstract class BaseExcelExporter {
    
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Tạo header style
     */
    protected CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    /**
     * Tạo data style
     */
    protected CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    /**
     * Tạo number style
     */
    protected CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
    
    /**
     * Tạo currency style
     */
    protected CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
    
    /**
     * Tạo date style
     */
    protected CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd/mm/yyyy"));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    /**
     * Set cell value với style
     */
    protected void setCellValue(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value != null ? value : "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    /**
     * Set cell value (number) với style
     */
    protected void setCellValue(Row row, int columnIndex, Number value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue(0);
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    /**
     * Set cell value (date) với style
     */
    protected void setCellValue(Row row, int columnIndex, LocalDate date, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (date != null) {
            cell.setCellValue(java.sql.Date.valueOf(date));
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    /**
     * Set cell value (datetime) với style
     */
    protected void setCellValue(Row row, int columnIndex, LocalDateTime dateTime, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (dateTime != null) {
            cell.setCellValue(java.sql.Timestamp.valueOf(dateTime));
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    /**
     * Set cell value (date string) với style
     */
    protected void setCellValueAsDate(Row row, int columnIndex, String dateString, CellStyle dateStyle, Workbook workbook) {
        if (dateString != null && !dateString.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(dateString);
                setCellValue(row, columnIndex, date, dateStyle);
            } catch (Exception e) {
                setCellValue(row, columnIndex, dateString, createDataStyle(workbook));
            }
        }
    }
    
    /**
     * Tạo header row
     */
    protected Row createHeaderRow(Sheet sheet, String[] headers, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        return headerRow;
    }
    
    /**
     * Auto-size columns
     */
    protected void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Ghi workbook ra file
     */
    protected void writeWorkbook(Workbook workbook, String filePath) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }
    }
    
    protected BaseExcelExporter() {
        // Base constructor
    }
    
    protected Workbook getWorkbook(Workbook workbook) {
        return workbook;
    }
    
    /**
     * Create total style
     */
    protected CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        return style;
    }
    
    /**
     * Create warning style
     */
    protected CellStyle createWarningStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    
    /**
     * Set cell value (BigDecimal) với style
     */
    protected void setCellValue(Row row, int columnIndex, java.math.BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue(0);
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    /**
     * Set cell value (Integer) với style
     */
    protected void setCellValue(Row row, int columnIndex, Integer value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (value != null) {
            cell.setCellValue(value);
        } else {
            cell.setCellValue(0);
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
    }
    
    /**
     * Format gender
     */
    protected String formatGender(String gender) {
        if (gender == null) return "";
        return switch (gender.toUpperCase()) {
            case "M", "MALE" -> "Nam";
            case "F", "FEMALE" -> "Nữ";
            default -> gender;
        };
    }
    
    /**
     * Format status
     */
    protected String formatStatus(String status) {
        if (status == null) return "";
        return switch (status.toUpperCase()) {
            case "ACTIVE" -> "Hoạt động";
            case "INACTIVE" -> "Không hoạt động";
            case "EXPIRED" -> "Hết hạn";
            case "PENDING" -> "Chờ xử lý";
            default -> status;
        };
    }
}

