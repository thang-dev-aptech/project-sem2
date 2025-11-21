package com.example.gympro.service.export;

import com.example.gympro.viewModel.PackageReport;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Exporter cho báo cáo gói tập
 */
public class PackageReportExporter extends BaseExcelExporter {
    
    public PackageReportExporter() {
        super();
    }
    
    /**
     * Xuất báo cáo gói tập ra Excel
     */
    public void export(List<PackageReport> reports, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo Gói tập");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            
            String[] headers = {"Tên gói", "Giá", "Số lượng bán", "Doanh thu", 
                               "Trung bình/HĐ", "Trạng thái"};
            createHeaderRow(sheet, headers, headerStyle);
            
            int rowNum = 1;
            int totalSold = 0;
            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (PackageReport report : reports) {
                Row row = sheet.createRow(rowNum++);
                
                setCellValue(row, 0, report.getPackageName(), dataStyle);
                setCellValue(row, 1, report.getPrice(), numberStyle);
                setCellValue(row, 2, report.getSoldCount(), numberStyle);
                setCellValue(row, 3, report.getRevenue(), numberStyle);
                setCellValue(row, 4, report.getAvgRevenue(), numberStyle);
                setCellValue(row, 5, report.getStatus(), dataStyle);
                
                totalSold += report.getSoldCount();
                if (report.getRevenue() != null) {
                    totalRevenue = totalRevenue.add(report.getRevenue());
                }
            }
            
            // Tổng cộng
            Row totalRow = sheet.createRow(rowNum);
            Cell totalLabelCell = totalRow.createCell(1);
            totalLabelCell.setCellValue("TỔNG:");
            totalLabelCell.setCellStyle(totalStyle);
            
            Cell totalSoldCell = totalRow.createCell(2);
            totalSoldCell.setCellValue(totalSold);
            totalSoldCell.setCellStyle(totalStyle);
            
            Cell totalRevenueCell = totalRow.createCell(3);
            totalRevenueCell.setCellValue(totalRevenue.doubleValue());
            totalRevenueCell.setCellStyle(totalStyle);
            
            autoSizeColumns(sheet, headers.length);
            writeWorkbook(workbook, filePath);
        }
    }
}

