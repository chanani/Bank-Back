package com.bank.gugu.global.provider.excel;

import com.bank.gugu.global.provider.excel.dto.ExcelSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelProvider {

    private final List<ExcelSheet> sheets;

    public ExcelProvider(List<ExcelSheet> sheets) {
        this.sheets = sheets;
    }

    public ByteArrayOutputStream create() throws IOException {
        Workbook workbook = new XSSFWorkbook();

        int startRowIndex = 1;
        int startColIndex = 1;

        for (ExcelSheet sheetData : sheets) {
            Sheet sheet = workbook.createSheet(sheetData.name());

            // 헤더
            Row headerRow = sheet.createRow(startRowIndex);
            List<String> headers = sheetData.headers();
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(startColIndex + i);
                cell.setCellValue(headers.get(i));
            }

            // 데이터
            List<List<Object>> rows = sheetData.rows();
            for (int i = 0; i < rows.size(); i++) {
                Row row = sheet.createRow(startRowIndex + 1 + i);
                List<Object> rowData = rows.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    Cell cell = row.createCell(startColIndex + j);
                    Object value = rowData.get(j);
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else {
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }
            }

            // 헤더를 통한 자동 열 너비 조정
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(startColIndex + i);
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out;
    }
}
