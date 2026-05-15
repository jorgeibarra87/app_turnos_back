package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.service.PlantillaExcelService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plantilla")
@AllArgsConstructor
public class PlantillaExcelController {

    private final PlantillaExcelService plantillaExcelService;

    @GetMapping("/excel")
    public ResponseEntity<?> descargarPlantilla(@RequestParam(defaultValue = "31") int dias) {
        try {
            var resource = plantillaExcelService.descargarPlantilla(dias);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plantilla_cuadro_turnos.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al generar plantilla: " + e.getMessage());
        }
    }
}
