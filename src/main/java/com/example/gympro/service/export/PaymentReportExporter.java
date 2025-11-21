package com.example.gympro.service.export;

import com.example.gympro.viewModel.PaymentReport;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Exporter cho báo cáo thanh toán
 */
public class PaymentReportExporter extends BaseExcelExporter {
    
    public PaymentReportExporter() {
        super();
    }
    
    /**
     * Xuất báo cáo thanh toán ra Excel
     */
    public void export(List<PaymentReport> reports, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo Thanh toán");
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            
            String[] headers = {"Mã HĐ", "Ngày", "Hội viên", "Số tiền", "Phương thức", "Ghi chú"};
            createHeaderRow(sheet, headers, headerStyle);
            
            int rowNum = 1;
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (PaymentReport report : reports) {
                Row row = sheet.createRow(rowNum++);
                
                setCellValue(row, 0, report.getInvoiceNo(), dataStyle);
                setCellValue(row, 1, report.getPaymentDate(), dateStyle);
                setCellValue(row, 2, report.getMemberName(), dataStyle);
                setCellValue(row, 3, report.getAmount(), numberStyle);
                setCellValue(row, 4, report.getMethodName(), dataStyle);
                setCellValue(row, 5, report.getNote(), dataStyle);
                
                if (report.getAmount() != null) {
                    totalAmount = totalAmount.add(report.getAmount());
                }
            }
            
            // Tổng cộng
            Row totalRow = sheet.createRow(rowNum);
            Cell totalLabelCell = totalRow.createCell(2);
            totalLabelCell.setCellValue("TỔNG CỘNG:");
            totalLabelCell.setCellStyle(totalStyle);
            
            Cell totalValueCell = totalRow.createCell(3);
            totalValueCell.setCellValue(totalAmount.doubleValue());
            totalValueCell.setCellStyle(totalStyle);
            
            autoSizeColumns(sheet, headers.length);
            writeWorkbook(workbook, filePath);
        }
    }
}

