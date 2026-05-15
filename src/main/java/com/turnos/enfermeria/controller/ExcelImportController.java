package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.response.MatrizMensualDTO;
import com.turnos.enfermeria.service.ExcelImportService;
import com.turnos.enfermeria.service.FullImportService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/importar")
@AllArgsConstructor
public class ExcelImportController {

    private final ExcelImportService excelImportService;
    private final FullImportService fullImportService;

    @PostMapping(value = "/excel/{idCuadroTurno}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importarExcel(
            @PathVariable Long idCuadroTurno,
            @RequestParam("file") MultipartFile file) {
        try {
            String validacion = excelImportService.validarEstructuraExcel(file);
            MatrizMensualDTO resultado = excelImportService.importarExcel(file, idCuadroTurno);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Importación exitosa",
                    "validacion", validacion,
                    "filas", resultado.getFilas().size(),
                    "cuadro", resultado.getNombreCuadro()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/excel/validar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> validarExcel(@RequestParam("file") MultipartFile file) {
        try {
            String resultado = excelImportService.validarEstructuraExcel(file);
            return ResponseEntity.ok(Map.of("valido", true, "reporte", resultado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("valido", false, "error", e.getMessage()));
        }
    }

    @PostMapping(value = "/completo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importarCompleto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("anio") String anio,
            @RequestParam("mes") String mes,
            @RequestParam(value = "entidad", required = false) String entidad,
            @RequestParam(value = "tipoPersonal", required = false) String tipoPersonal,
            @RequestParam(value = "idEquipo", required = false) Long idEquipo,
            @RequestParam(value = "categoria", required = false, defaultValue = "servicio") String categoria,
            @RequestParam(value = "idMacroproceso", required = false) Long idMacroproceso,
            @RequestParam(value = "idProceso", required = false) Long idProceso,
            @RequestParam(value = "idServicio", required = false) Long idServicio,
            @RequestParam(value = "idSeccionServicio", required = false) Long idSeccionServicio,
            @RequestParam(value = "idSubseccionServicio", required = false) Long idSubseccionServicio,
            @RequestParam(value = "observaciones", required = false) String observaciones) {
        try {
            Map<String, Object> resultado = fullImportService.importarCompleto(
                    file, anio, mes, entidad, tipoPersonal, idEquipo, categoria,
                    idMacroproceso, idProceso, idServicio, idSeccionServicio,
                    idSubseccionServicio, observaciones);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
