package com.example.gympro.service.export;

import com.example.gympro.viewModel.ExpiringMember;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

/**
 * Exporter cho danh sách hội viên sắp hết hạn
 */
public class ExpiringMemberExporter extends BaseExcelExporter {
    
    public ExpiringMemberExporter() {
        super();
    }
    
    /**
     * Xuất danh sách hội viên sắp hết hạn ra Excel
     */
    public void export(List<ExpiringMember> members, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hội viên sắp hết hạn");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle warningStyle = createWarningStyle(workbook);
            
            String[] headers = {"Mã HV", "Họ tên", "SĐT", "Gói tập", "Ngày hết hạn", 
                               "Số ngày còn lại", "Trạng thái"};
            createHeaderRow(sheet, headers, headerStyle);
            
            int rowNum = 1;
            for (ExpiringMember member : members) {
                Row row = sheet.createRow(rowNum++);
                
                CellStyle rowStyle = member.getDaysLeft() <= 3 ? warningStyle : dataStyle;
                
                setCellValue(row, 0, member.getId(), rowStyle);
                setCellValue(row, 1, member.getName(), rowStyle);
                setCellValue(row, 2, member.getPhone(), rowStyle);
                setCellValue(row, 3, member.getPackageName(), rowStyle);
                setCellValueAsDate(row, 4, member.getExpiry(), rowStyle, workbook);
                
                Cell daysCell = row.createCell(5);
                daysCell.setCellValue(member.getDaysLeft());
                daysCell.setCellStyle(numberStyle);
                
                setCellValue(row, 6, member.getStatus(), rowStyle);
            }
            
            autoSizeColumns(sheet, headers.length);
            writeWorkbook(workbook, filePath);
        }
    }
}

