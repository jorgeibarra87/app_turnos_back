package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.model.dto.response.EntidadDTO;
import com.turnos.enfermeria.service.EntidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entidad")
@Tag(name = "Entidades", description = "Operaciones relacionadas con las entidades/sindicatos")
@AllArgsConstructor
public class EntidadController {

    private final EntidadService entidadService;

    @PostMapping
    @Operation(summary = "Crear entidad", description = "Registra una nueva entidad o sindicato")
    public ResponseEntity<EntidadDTO> create(@RequestBody EntidadDTO entidadDTO) {
        EntidadDTO nueva = entidadService.create(entidadDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping
    @Operation(summary = "Listar entidades", description = "Devuelve todas las entidades registradas")
    public ResponseEntity<List<EntidadDTO>> findAll() {
        List<EntidadDTO> lista = entidadService.findAll();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener entidad por ID")
    public ResponseEntity<EntidadDTO> findById(@PathVariable Long id) {
        return entidadService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Entidad no encontrada: " + id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar entidad")
    public ResponseEntity<EntidadDTO> update(@RequestBody EntidadDTO entidadDTO, @PathVariable Long id) {
        return entidadService.findById(id)
                .map(existente -> ResponseEntity.ok(entidadService.update(entidadDTO, id)))
                .orElseThrow(() -> new EntityNotFoundException("Entidad no encontrada: " + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar entidad")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        entidadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
