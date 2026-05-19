package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.service.ReporteExcelService;
import com.turnos.enfermeria.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/reportes")
@Tag(name = "Reportes", description = "Operaciones relacionadas con la Generacion de Reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final ReporteExcelService reporteExcelService;

    @Operation(
            summary = "Generar reporte de turnos con filtros",
            description = "Genera el reporte de los turnos que tienen las personas en base al mes, el cuadro de turno y la persona.",
            tags={"Reportes"}
    )
    @GetMapping("/{anio}/{mes}/{cuadroId}")
    public ResponseEntity<Map<String, Object>> generarReporte(
            @PathVariable int anio,
            @PathVariable int mes,
            @PathVariable Long cuadroId) {

        Map<String, Object> reporte = reporteService.generarReporte(anio, mes, cuadroId);
        return ResponseEntity.ok(reporte);
    }

    @Operation(
            summary = "Exportar reporte a Excel con gráfico",
            description = "Descarga un archivo Excel con los turnos filtrados y un gráfico de barras de horas por usuario.",
            tags = {"Reportes"}
    )
    @GetMapping("/{anio}/{mes}/{cuadroId}/excel")
    public ResponseEntity<InputStreamResource> exportarExcel(
            @PathVariable int anio,
            @PathVariable int mes,
            @PathVariable Long cuadroId) throws Exception {

        InputStreamResource excel = reporteExcelService.exportarExcel(anio, mes, cuadroId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ReporteTurnos_" + anio + "_" + mes + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}