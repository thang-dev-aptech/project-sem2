package com.example.gympro.service.export;

import com.example.gympro.viewModel.RevenueReport;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Exporter cho báo cáo doanh thu
 */
public class RevenueReportExporter extends BaseExcelExporter {
    
    public RevenueReportExporter() {
        super();
    }
    
    /**
     * Xuất báo cáo doanh thu ra Excel
     */
    public void export(List<RevenueReport> reports, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo Doanh thu");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            
            String[] headers = {"Số HĐ", "Ngày", "Hội viên", "Mã HV", "Gói tập", 
                               "Tạm tính", "Giảm giá", "Tổng cộng", "Trạng thái"};
            createHeaderRow(sheet, headers, headerStyle);
            
            int rowNum = 1;
            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (RevenueReport report : reports) {
                Row row = sheet.createRow(rowNum++);
                
                setCellValue(row, 0, report.getInvoiceNo(), dataStyle);
                setCellValue(row, 1, report.getInvoiceDate(), dateStyle);
                setCellValue(row, 2, report.getMemberName(), dataStyle);
                setCellValue(row, 3, report.getMemberCode(), dataStyle);
                setCellValue(row, 4, report.getPackageName(), dataStyle);
                setCellValue(row, 5, report.getSubtotal(), numberStyle);
                setCellValue(row, 6, report.getDiscount(), numberStyle);
                setCellValue(row, 7, report.getTotal(), numberStyle);
                setCellValue(row, 8, report.getStatus(), dataStyle);
                
                if (report.getTotal() != null) {
                    totalRevenue = totalRevenue.add(report.getTotal());
                }
            }
            
            // Tổng cộng
            Row totalRow = sheet.createRow(rowNum);
            Cell totalLabelCell = totalRow.createCell(6);
            totalLabelCell.setCellValue("TỔNG CỘNG:");
            totalLabelCell.setCellStyle(totalStyle);
            
            Cell totalValueCell = totalRow.createCell(7);
            totalValueCell.setCellValue(totalRevenue.doubleValue());
            totalValueCell.setCellStyle(totalStyle);
            
            autoSizeColumns(sheet, headers.length);
            writeWorkbook(workbook, filePath);
        }
    }
}

