package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.response.PersonaContratoDTO;
import com.turnos.enfermeria.model.dto.response.PersonaContratoTotalDTO;
import com.turnos.enfermeria.service.PersonaContratoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/personaContrato")
public class PersonaContratoController {
    @Autowired
    private PersonaContratoService personaContratoService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    @Operation(
            summary = "Crear un nuevo persona contrato",
            description = "Registra un nuevo persona contrato con sus datos personales y vinculaciones.",
            tags={"Contratos"}
    )
    public ResponseEntity<PersonaContratoDTO> create(@RequestBody PersonaContratoDTO personaContratoDTO){
        try {
            PersonaContratoDTO nuevoPersonaContratoDTO = personaContratoService.create(personaContratoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPersonaContratoDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.PERSONA_CONTRATO_NO_ENCONTRADO,
                    personaContratoDTO.getIdPersonaContrato(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.PERSONA_CONTRATO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.PERSONA_CONTRATO_ESTADO_INVALIDO,
                    "No se pudo crear persona contrato: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los personas contrato",
            description = "Devuelve una lista con todos los personas contrato registrados en el sistema.",
            tags={"Contratos"}
    )
    public ResponseEntity<List<PersonaContratoDTO>> findAll(){
        List<PersonaContratoDTO> personaContratoDTO = personaContratoService.findAll();
        return personaContratoDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(personaContratoDTO);
    }

    @GetMapping("/{idPersonaContrato}")
    @Operation(
            summary = "Buscar persona contrato por ID",
            description = "Devuelve la información de un persona contrato a partir de su ID.",
            tags={"Contratos"}
    )
    public ResponseEntity<PersonaContratoDTO> findById(@PathVariable Long idPersonaContrato){
        return personaContratoService.findById(idPersonaContrato)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PERSONA_CONTRATO_NO_ENCONTRADO,
                        idPersonaContrato,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idPersonaContrato}")
    @Operation(summary = "Actualizar persona contrato",description = "Actualiza los datos de un persona contrato existente.",
            tags={"Contratos"})
    public ResponseEntity<PersonaContratoDTO> update(@RequestBody PersonaContratoDTO personaContratoDTO, @PathVariable Long idPersonaContrato){
        return personaContratoService.findById(idPersonaContrato)
                .map(personaContratoExistente -> ResponseEntity.ok(personaContratoService.update(personaContratoDTO, idPersonaContrato)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.PERSONA_CONTRATO_NO_ENCONTRADO,
                        idPersonaContrato,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @Operation(summary = "Consultar personas contrato por documento",description = "Consultar los datos de un persona contrato existente, por documento",
            tags={"Contratos"})
    @GetMapping("/info/{documento}")
    public ResponseEntity<PersonaContratoTotalDTO> obtenerInformacionPersona(@PathVariable String documento) {
        try {
            PersonaContratoTotalDTO dto = personaContratoService.obtenerInformacionPersonaCompleta(documento);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
