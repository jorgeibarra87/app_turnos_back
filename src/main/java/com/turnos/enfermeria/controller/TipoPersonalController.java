package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.response.TipoPersonalDTO;
import com.turnos.enfermeria.service.TipoPersonalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipo-personal")
@Tag(name = "Tipos de Personal", description = "Operaciones relacionadas con los tipos de personal")
@AllArgsConstructor
public class TipoPersonalController {

    private final TipoPersonalService tipoPersonalService;

    @PostMapping
    @Operation(summary = "Crear tipo de personal")
    public ResponseEntity<TipoPersonalDTO> create(@RequestBody TipoPersonalDTO tipoPersonalDTO) {
        TipoPersonalDTO nuevo = tipoPersonalService.create(tipoPersonalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping
    @Operation(summary = "Listar tipos de personal")
    public ResponseEntity<List<TipoPersonalDTO>> findAll() {
        List<TipoPersonalDTO> lista = tipoPersonalService.findAll();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo de personal por ID")
    public ResponseEntity<TipoPersonalDTO> findById(@PathVariable Long id) {
        return tipoPersonalService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de personal no encontrado: " + id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo de personal")
    public ResponseEntity<TipoPersonalDTO> update(@RequestBody TipoPersonalDTO tipoPersonalDTO, @PathVariable Long id) {
        return tipoPersonalService.findById(id)
                .map(existente -> ResponseEntity.ok(tipoPersonalService.update(tipoPersonalDTO, id)))
                .orElseThrow(() -> new EntityNotFoundException("Tipo de personal no encontrado: " + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo de personal")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoPersonalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
