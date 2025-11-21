package com.example.gympro.service.export;

import com.example.gympro.viewModel.Package;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

/**
 * Exporter cho danh sách gói tập
 */
public class PackageExporter extends BaseExcelExporter {
    
    public PackageExporter() {
        super();
    }
    
    /**
     * Xuất danh sách gói tập ra Excel
     */
    public void export(List<Package> packages, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách Gói tập");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            
            String[] headers = {"Mã", "Tên gói", "Thời hạn (ngày)", "Giá", "Trạng thái", 
                               "Mô tả", "Ngày tạo", "Cập nhật"};
            createHeaderRow(sheet, headers, headerStyle);
            
            int rowNum = 1;
            for (Package pkg : packages) {
                Row row = sheet.createRow(rowNum++);
                
                setCellValue(row, 0, pkg.getCode(), dataStyle);
                setCellValue(row, 1, pkg.getName(), dataStyle);
                setCellValue(row, 2, pkg.getDurationDays(), numberStyle);
                setCellValue(row, 3, pkg.getPrice(), numberStyle);
                setCellValue(row, 4, pkg.isActive() ? "Hiển thị" : "Ẩn", dataStyle);
                setCellValue(row, 5, pkg.getDescription(), dataStyle);
                setCellValue(row, 6, pkg.getCreatedAt(), dateStyle);
                setCellValue(row, 7, pkg.getUpdatedAt(), dateStyle);
            }
            
            autoSizeColumns(sheet, headers.length);
            writeWorkbook(workbook, filePath);
        }
    }
}

