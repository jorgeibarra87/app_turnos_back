package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.response.TipoJornadaDTO;
import com.turnos.enfermeria.service.TipoJornadaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipo-jornada")
@AllArgsConstructor
public class TipoJornadaController {

    private final TipoJornadaService tipoJornadaService;

    @GetMapping
    public ResponseEntity<List<TipoJornadaDTO>> findAll() {
        return ResponseEntity.ok(tipoJornadaService.findActivos());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<TipoJornadaDTO>> findAllIncluyendoInactivos() {
        return ResponseEntity.ok(tipoJornadaService.findAll());
    }

    @GetMapping("/trabajo")
    public ResponseEntity<List<TipoJornadaDTO>> findTrabajo() {
        return ResponseEntity.ok(tipoJornadaService.findTrabajo());
    }

    @GetMapping("/descanso")
    public ResponseEntity<List<TipoJornadaDTO>> findDescanso() {
        return ResponseEntity.ok(tipoJornadaService.findDescanso());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<TipoJornadaDTO> findById(@PathVariable String codigo) {
        return tipoJornadaService.findById(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoJornadaDTO> create(@RequestBody TipoJornadaDTO dto) {
        return new ResponseEntity<>(tipoJornadaService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<TipoJornadaDTO> update(@PathVariable String codigo, @RequestBody TipoJornadaDTO dto) {
        return ResponseEntity.ok(tipoJornadaService.update(codigo, dto));
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> delete(@PathVariable String codigo) {
        tipoJornadaService.delete(codigo);
        return ResponseEntity.noContent().build();
    }
}
