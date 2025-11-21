package com.example.gympro.service.export;

import com.example.gympro.viewModel.MemberReport;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

/**
 * Exporter cho báo cáo hội viên
 */
public class MemberReportExporter extends BaseExcelExporter {
    
    public MemberReportExporter() {
        super();
    }
    
    /**
     * Xuất báo cáo hội viên ra Excel
     */
    public void export(List<MemberReport> reports, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo Hội viên");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            
            String[] headers = {"Mã HV", "Họ tên", "SĐT", "Email", "Trạng thái", 
                               "Gói tập", "Ngày bắt đầu", "Ngày kết thúc", "Ngày tạo"};
            createHeaderRow(sheet, headers, headerStyle);
            
            int rowNum = 1;
            for (MemberReport report : reports) {
                Row row = sheet.createRow(rowNum++);
                
                setCellValue(row, 0, report.getMemberCode(), dataStyle);
                setCellValue(row, 1, report.getMemberName(), dataStyle);
                setCellValue(row, 2, report.getPhone(), dataStyle);
                setCellValue(row, 3, report.getEmail(), dataStyle);
                setCellValue(row, 4, report.getStatus(), dataStyle);
                setCellValue(row, 5, report.getPackageName(), dataStyle);
                setCellValue(row, 6, report.getStartDate(), dateStyle);
                setCellValue(row, 7, report.getEndDate(), dateStyle);
                setCellValue(row, 8, report.getCreatedAt(), dateStyle);
            }
            
            autoSizeColumns(sheet, headers.length);
            writeWorkbook(workbook, filePath);
        }
    }
}

