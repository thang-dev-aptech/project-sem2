package com.example.gympro.service.export;

import com.example.gympro.viewModel.Member;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

/**
 * Exporter cho danh sách hội viên
 */
public class MemberExporter extends BaseExcelExporter {
    
    public MemberExporter() {
        super();
    }
    
    /**
     * Xuất danh sách hội viên ra Excel
     */
    public void export(List<Member> members, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách Hội viên");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            
            String[] headers = {"Mã HV", "Họ Tên", "SĐT", "Email", "Giới tính", "Ngày sinh", 
                               "Địa chỉ", "Trạng thái", "Ghi chú", "Ngày tạo", "Cập nhật"};
            createHeaderRow(sheet, headers, headerStyle);
            
            int rowNum = 1;
            for (Member member : members) {
                Row row = sheet.createRow(rowNum++);
                
                setCellValue(row, 0, member.getMemberCode(), dataStyle);
                setCellValue(row, 1, member.getFullName(), dataStyle);
                setCellValue(row, 2, member.getPhone(), dataStyle);
                setCellValue(row, 3, member.getEmail(), dataStyle);
                setCellValue(row, 4, formatGender(member.getGender()), dataStyle);
                setCellValue(row, 5, member.getDob(), dateStyle);
                setCellValue(row, 6, member.getAddress(), dataStyle);
                setCellValue(row, 7, formatStatus(member.getStatus()), dataStyle);
                setCellValue(row, 8, member.getNote(), dataStyle);
                setCellValue(row, 9, member.getCreatedAt(), dateStyle);
                setCellValue(row, 10, member.getUpdatedAt(), dateStyle);
            }
            
            autoSizeColumns(sheet, headers.length);
            writeWorkbook(workbook, filePath);
        }
    }
}

