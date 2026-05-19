package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.model.entity.CuadroTurno;
import com.turnos.enfermeria.model.entity.Turnos;
import com.turnos.enfermeria.repository.CuadroTurnoRepository;
import com.turnos.enfermeria.repository.TurnosRepository;
import com.turnos.enfermeria.service.ReporteExcelService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReporteExcelServiceImpl implements ReporteExcelService {

    private final TurnosRepository turnoRepository;
    private final CuadroTurnoRepository cuadroTurnoRepository;

    @Override
    public InputStreamResource exportarExcel(int anio, int mes, Long cuadroTurnoId) throws Exception {
        Optional<CuadroTurno> cuadroOpt = cuadroTurnoRepository.findById(cuadroTurnoId);
        String nombreCuadro = cuadroOpt.map(CuadroTurno::getNombre).orElse("CUADRO DE TURNOS");

        List<Turnos> turnos = turnoRepository.findByCuadroTurno_IdCuadroTurno(cuadroTurnoId).stream()
                .filter(t -> t.getFechaInicio().getYear() == anio &&
                        t.getFechaInicio().getMonthValue() == mes)
                .collect(Collectors.toList());

        String[] meses = {"", "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
                "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
        String nombreMes = meses[mes];

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            // ---- SHEET: Reporte ----
            XSSFSheet reportSheet = workbook.createSheet("Reporte");

            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dataLeftStyle = workbook.createCellStyle();
            dataLeftStyle.cloneStyleFrom(dataStyle);
            dataLeftStyle.setAlignment(HorizontalAlignment.LEFT);

            CellStyle totalStyle = workbook.createCellStyle();
            totalStyle.setFont(boldFont);
            totalStyle.setBorderBottom(BorderStyle.THIN);
            totalStyle.setBorderTop(BorderStyle.THIN);
            totalStyle.setBorderLeft(BorderStyle.THIN);
            totalStyle.setBorderRight(BorderStyle.THIN);
            totalStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font footerFont = workbook.createFont();
            footerFont.setItalic(true);
            footerFont.setFontHeightInPoints((short) 9);
            CellStyle footerStyle = workbook.createCellStyle();
            footerStyle.setFont(footerFont);

            int r = 0;

            Row titleRow = reportSheet.createRow(r++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE TURNOS - " + nombreMes + " " + anio);
            titleCell.setCellStyle(titleStyle);
            reportSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            Row cuadroRow = reportSheet.createRow(r++);
            Cell cuadroCell = cuadroRow.createCell(0);
            cuadroCell.setCellValue("Cuadro: " + nombreCuadro.toUpperCase());
            cuadroCell.setCellStyle(workbook.createCellStyle());
            reportSheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

            r++;

            String[] headers = {"Usuario", "Jornada", "Fecha Inicio", "Hora Inicio", "Fecha Fin", "Hora Fin", "Horas"};
            Row headerRow = reportSheet.createRow(r++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            Map<String, List<Turnos>> porUsuario = turnos.stream()
                    .collect(Collectors.groupingBy(t ->
                            t.getUsuario() != null ? t.getUsuario().getNombreCompleto() : "Sin asignar",
                            LinkedHashMap::new, Collectors.toList()));

            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

            for (Map.Entry<String, List<Turnos>> entry : porUsuario.entrySet()) {
                List<Turnos> userTurnos = entry.getValue();
                userTurnos.sort(Comparator.comparing(Turnos::getFechaInicio));

                for (Turnos t : userTurnos) {
                    Row row = reportSheet.createRow(r++);
                    row.createCell(0).setCellValue(entry.getKey());
                    row.getCell(0).setCellStyle(dataLeftStyle);
                    row.createCell(1).setCellValue(t.getJornada() != null ? t.getJornada() : "N/A");
                    row.getCell(1).setCellStyle(dataStyle);
                    row.createCell(2).setCellValue(t.getFechaInicio() != null ? t.getFechaInicio().format(dateFmt) : "");
                    row.getCell(2).setCellStyle(dataStyle);
                    row.createCell(3).setCellValue(t.getFechaInicio() != null ? t.getFechaInicio().format(timeFmt) : "");
                    row.getCell(3).setCellStyle(dataStyle);
                    row.createCell(4).setCellValue(t.getFechaFin() != null ? t.getFechaFin().format(dateFmt) : "");
                    row.getCell(4).setCellStyle(dataStyle);
                    row.createCell(5).setCellValue(t.getFechaFin() != null ? t.getFechaFin().format(timeFmt) : "");
                    row.getCell(5).setCellStyle(dataStyle);
                    row.createCell(6).setCellValue(t.getTotalHoras() != null ? t.getTotalHoras() : 0);
                    row.getCell(6).setCellStyle(dataStyle);
                }

                long totalHorasUser = userTurnos.stream()
                        .mapToLong(t -> t.getTotalHoras() != null ? t.getTotalHoras() : 0).sum();
                Row totalRow = reportSheet.createRow(r++);
                Cell totalCell = totalRow.createCell(0);
                totalCell.setCellValue("→ Total " + entry.getKey() + ": " + userTurnos.size() + " turnos, " + totalHorasUser + " horas");
                totalCell.setCellStyle(totalStyle);
                for (int i = 1; i < 7; i++) {
                    Cell c = totalRow.createCell(i);
                    c.setCellStyle(totalStyle);
                }
                reportSheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 6));
                r++;
            }

            long totalTurnos = turnos.size();
            long totalHoras = turnos.stream().mapToLong(t -> t.getTotalHoras() != null ? t.getTotalHoras() : 0).sum();
            Row grandTotalRow = reportSheet.createRow(r++);
            Cell grandCell = grandTotalRow.createCell(0);
            grandCell.setCellValue("TOTAL GENERAL: " + totalTurnos + " turnos, " + totalHoras + " horas");
            grandCell.setCellStyle(totalStyle);
            for (int i = 1; i < 7; i++) {
                Cell c = grandTotalRow.createCell(i);
                c.setCellStyle(totalStyle);
            }
            reportSheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 6));

            r++;
            Row footerRow = reportSheet.createRow(r);
            Cell footerCell = footerRow.createCell(0);
            footerCell.setCellValue("Generado el " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                    " - Para uso exclusivo de trámites y servicios prestados por Hospital Universitario San José de Popayán");
            footerCell.setCellStyle(footerStyle);
            reportSheet.addMergedRegion(new CellRangeAddress(r, r, 0, 6));

            reportSheet.setColumnWidth(0, 45 * 256);
            reportSheet.setColumnWidth(1, 14 * 256);
            reportSheet.setColumnWidth(2, 16 * 256);
            reportSheet.setColumnWidth(3, 14 * 256);
            reportSheet.setColumnWidth(4, 16 * 256);
            reportSheet.setColumnWidth(5, 14 * 256);
            reportSheet.setColumnWidth(6, 10 * 256);

            // ---- SHEET: Datos para gráfico ----
            XSSFSheet chartDataSheet = workbook.createSheet("Horas por Usuario");

            Row chartHeaderRow = chartDataSheet.createRow(0);
            Cell ch1 = chartHeaderRow.createCell(0);
            ch1.setCellValue("Usuario");
            ch1.setCellStyle(headerStyle);
            Cell ch2 = chartHeaderRow.createCell(1);
            ch2.setCellValue("Horas");
            ch2.setCellStyle(headerStyle);

            int chartRowIdx = 1;
            for (Map.Entry<String, List<Turnos>> entry : porUsuario.entrySet()) {
                long horas = entry.getValue().stream()
                        .mapToLong(t -> t.getTotalHoras() != null ? t.getTotalHoras() : 0).sum();
                Row row = chartDataSheet.createRow(chartRowIdx++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(horas);
            }

            chartDataSheet.setColumnWidth(0, 50 * 256);
            chartDataSheet.setColumnWidth(1, 12 * 256);

            // ---- Bar Chart ----
            XSSFDrawing drawing = chartDataSheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 2, 0, 15, 20);
            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("Horas por Usuario - " + nombreMes + " " + anio);
            chart.setTitleOverlay(false);

            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.TOP_RIGHT);

            XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            categoryAxis.setTitle("Usuario");

            XDDFValueAxis valueAxis = chart.createValueAxis(AxisPosition.LEFT);
            valueAxis.setTitle("Horas");

            XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(
                    chartDataSheet, new CellRangeAddress(1, porUsuario.size(), 0, 0));
            XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(
                    chartDataSheet, new CellRangeAddress(1, porUsuario.size(), 1, 1));

            XDDFChartData data = chart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
            XDDFBarChartData barData = (XDDFBarChartData) data;
            barData.setBarDirection(BarDirection.COL);

            XDDFChartData.Series series = data.addSeries(categories, values);
            series.setTitle("Horas", null);

            chart.plot(data);

            // ---- Write ----
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
        }
    }
}
