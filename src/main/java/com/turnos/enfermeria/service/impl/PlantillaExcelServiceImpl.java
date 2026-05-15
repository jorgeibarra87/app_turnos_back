package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.service.PlantillaExcelService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
@AllArgsConstructor
public class PlantillaExcelServiceImpl implements PlantillaExcelService {

    @Override
    public InputStreamResource descargarPlantilla(int diasDelMes) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("CUADRO DE TURNOS");

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(boldFont);
            Font whiteFont = workbook.createFont();
            whiteFont.setColor(IndexedColors.WHITE.getIndex());
            whiteFont.setBold(true);
            headerStyle.setFont(whiteFont);

            Row headerRow = sheet.createRow(0);
            String[] columnasBase = {"SERVICIO", "NOMBRE EMPLEADO", "CEDULA", "PERFIL"};
            for (int i = 0; i < columnasBase.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnasBase[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int dia = 1; dia <= diasDelMes; dia++) {
                Cell cell = headerRow.createCell(3 + dia);
                cell.setCellValue(String.valueOf(dia));
                cell.setCellStyle(headerStyle);
            }

            CellStyle legendStyle = workbook.createCellStyle();
            legendStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            legendStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row legendRow = sheet.createRow(1);
            Cell legendCell = legendRow.createCell(0);
            legendCell.setCellValue("CÓDIGOS: M=Mañana(06-14) | T=Tarde(14-22) | N=Noche(22-06) | L=Libre | C=Compensatorio | D=Diurno(07-17)");
            legendCell.setCellStyle(legendStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 3 + diasDelMes));

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            for (int dia = 1; dia <= diasDelMes; dia++) {
                sheet.setColumnWidth(3 + dia, 5 * 256);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
        }
    }
}
