package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.request.ProgramacionDiariaRequest;
import com.turnos.enfermeria.model.dto.response.MatrizMensualDTO;
import com.turnos.enfermeria.service.ProgramacionDiariaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/programacion-diaria")
@AllArgsConstructor
public class ProgramacionDiariaController {

    private final ProgramacionDiariaService programacionDiariaService;

    @GetMapping("/cuadro/{idCuadroTurno}")
    public ResponseEntity<MatrizMensualDTO> obtenerMatriz(@PathVariable Long idCuadroTurno) {
        return ResponseEntity.ok(programacionDiariaService.obtenerMatrizPorCuadro(idCuadroTurno));
    }

    @PostMapping("/cuadro/{idCuadroTurno}")
    public ResponseEntity<MatrizMensualDTO> guardarMatriz(
            @PathVariable Long idCuadroTurno,
            @RequestBody ProgramacionDiariaRequest request) {
        request.setIdCuadroTurno(idCuadroTurno);
        return new ResponseEntity<>(programacionDiariaService.guardarMatrizCompleta(request), HttpStatus.CREATED);
    }

    @PutMapping("/cuadro/{idCuadroTurno}/celda")
    public ResponseEntity<Void> actualizarCelda(
            @PathVariable Long idCuadroTurno,
            @RequestBody Map<String, Object> body) {
        Long idPersona = Long.valueOf(body.get("idPersona").toString());
        Integer diaMes = Integer.valueOf(body.get("diaMes").toString());
        String codigoJornada = (String) body.get("codigoJornada");
        String observacion = (String) body.get("observacion");
        programacionDiariaService.actualizarCelda(idCuadroTurno, idPersona, diaMes, codigoJornada, observacion);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cuadro/{idCuadroTurno}")
    public ResponseEntity<Void> eliminarMatriz(@PathVariable Long idCuadroTurno) {
        programacionDiariaService.eliminarMatriz(idCuadroTurno);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cuadro/{idCuadroTurno}/generar-turnos")
    public ResponseEntity<Void> generarTurnos(@PathVariable Long idCuadroTurno) {
        programacionDiariaService.generarTurnosDesdeMatriz(idCuadroTurno);
        return ResponseEntity.ok().build();
    }
}
