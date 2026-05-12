package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.mapper.PersonasEquipoMapper;
import com.turnos.enfermeria.mapper.PersonasRolMapper;
import com.turnos.enfermeria.mapper.PersonasTituloMapper;
import com.turnos.enfermeria.model.dto.response.*;
import com.turnos.enfermeria.model.dto.request.LoginDTO;
import com.turnos.enfermeria.model.entity.Persona;
import com.turnos.enfermeria.repository.TitulosFormacionAcademicaRepository;
import com.turnos.enfermeria.service.PersonaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/persona")
@Tag(name = "Personas", description = "Operaciones relacionadas con la gestión de personas, roles, equipos y títulos académicos")
public class PersonaController {

    private static final Logger log = LoggerFactory.getLogger(PersonaController.class);

    @Autowired
    private PersonaService personaService;

    @Autowired
    private TitulosFormacionAcademicaRepository tituloRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private PersonasTituloMapper personasTituloMapper;

    @Autowired
    private PersonasRolMapper personasRolMapper;

    @Autowired
    private PersonasEquipoMapper personasEquipoMapper;

    // ========== CRUD BASICO ==========

    @PostMapping
    @Operation(summary = "Crear una nueva persona", description = "Registra una nueva persona en el sistema.")
    public ResponseEntity<PersonaDTO> create(@RequestBody PersonaDTO personaDTO) {
        try {
            PersonaDTO nuevaPersonaDTO = personaService.create(personaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPersonaDTO);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.PERSONA_NO_ENCONTRADA, personaDTO.getIdPersona(), request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.PERSONA_ESTADO_INVALIDO, "No se pudo crear persona: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al crear persona: {}", e.getMostSpecificCause().getMessage(), e);
            throw new GenericBadRequestException(CodigoError.PERSONA_DATOS_INVALIDOS, "Error de integridad de datos: " + e.getMostSpecificCause().getMessage(), request.getMethod(), request.getRequestURI());
        } catch (Exception e) {
            log.error("Error inesperado al crear persona: {}", e.getMessage(), e);
            throw new GenericBadRequestException(CodigoError.PERSONA_DATOS_INVALIDOS, "Error inesperado: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @GetMapping
    @Operation(summary = "Listar todas las personas", description = "Devuelve una lista con todas las personas registradas.")
    public ResponseEntity<List<PersonaDTO>> findAll() {
        List<PersonaDTO> personaDTO = personaService.findAll();
        return personaDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(personaDTO);
    }

    @GetMapping("/{idPersona}")
    @Operation(summary = "Buscar persona por ID", description = "Devuelve los datos de una persona específica.")
    public ResponseEntity<PersonaDTO> findById(@PathVariable Long idPersona) {
        return personaService.findById(idPersona)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(CodigoError.PERSONA_NO_ENCONTRADA, idPersona, request.getMethod(), request.getRequestURI()));
    }

    @PutMapping("/{idPersona}")
    @Operation(summary = "Actualizar datos de una persona", description = "Actualiza los datos personales de una persona existente.")
    public ResponseEntity<PersonaDTO> update(@RequestBody PersonaDTO personaDTO, @PathVariable Long idPersona) {
        return personaService.findById(idPersona)
                .map(personaExistente -> ResponseEntity.ok(personaService.update(personaDTO, idPersona)))
                .orElseThrow(() -> new GenericNotFoundException(CodigoError.PERSONA_NO_ENCONTRADA, idPersona, request.getMethod(), request.getRequestURI()));
    }

    // ========== EQUIPO ENDPOINTS ==========

    @GetMapping("/{id}/equipo")
    @Operation(summary = "Obtener equipos de una persona", description = "Lista todos los equipos asociados a una persona.")
    public ResponseEntity<List<EquipoDTO>> obtenerEquiposDePersona(@PathVariable Long id) {
        try {
            List<EquipoDTO> equipos = personaService.obtenerEquiposDeUsuario(id);
            return ResponseEntity.ok(equipos);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.EQUIPO_NO_ENCONTRADO, id, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.EQUIPO_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.EQUIPO_ESTADO_INVALIDO, "No se pudo acceder equipo: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @GetMapping("/equipo/{idEquipo}/usuarios")
    @Operation(summary = "Obtener personas de un equipo", description = "Lista todas las personas asociadas a un equipo.")
    public ResponseEntity<List<PersonaEquipoDTO>> obtenerPersonasPorEquipo(@PathVariable Long idEquipo) {
        List<PersonaEquipoDTO> personas = personaService.obtenerUsuariosPorEquipo(idEquipo);
        return ResponseEntity.ok(personas);
    }

    @PostMapping("/{id}/equipo/{idEquipo}")
    @Operation(summary = "Asignar equipo a persona", description = "Agrega un equipo existente a una persona.")
    public ResponseEntity<PersonaEquipoDTO> agregarEquipoAPersona(@PathVariable Long id, @PathVariable Long idEquipo) {
        try {
            PersonaEquipoDTO dto = personaService.agregarEquipoAUsuario(id, idEquipo);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.EQUIPO_NO_ENCONTRADO, id, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.EQUIPO_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.EQUIPO_ESTADO_INVALIDO, "No se pudo acceder equipo: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @PostMapping("/equipo/{idEquipo}/persona/{idPersona}")
    @Operation(summary = "Asignar persona a equipo", description = "Agrega una persona existente a un equipo.")
    public ResponseEntity<EquipoDTO> agregarPersonaAEquipo(@PathVariable Long idEquipo, @PathVariable Long idPersona) {
        try {
            EquipoDTO equipoDTO = personaService.agregarUsuarioAEquipo(idEquipo, idPersona);
            return ResponseEntity.ok(equipoDTO);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.PERSONA_NO_ENCONTRADA, idEquipo, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.PERSONA_ESTADO_INVALIDO, "No se pudo crear persona: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @PutMapping("/equipo/{idEquipo}")
    @Operation(summary = "Actualizar personas de un equipo", description = "Reemplaza la lista de personas asociadas a un equipo.")
    public ResponseEntity<EquipoDTO> actualizarPersonasDeEquipo(
            @PathVariable Long idEquipo,
            @RequestBody List<Long> nuevasPersonasIds) {
        try {
            EquipoDTO equipoDTO = personaService.actualizarUsuariosDeEquipo(idEquipo, nuevasPersonasIds);
            return ResponseEntity.ok(equipoDTO);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.PERSONA_NO_ENCONTRADA, idEquipo, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.PERSONA_ESTADO_INVALIDO, "No se pudo crear persona: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @PutMapping("/{idPersona}/equipo")
    @Operation(summary = "Actualizar equipos de una persona", description = "Reemplaza la lista de equipos de una persona.")
    public ResponseEntity<PersonaEquipoDTO> actualizarEquiposDePersona(
            @PathVariable Long idPersona,
            @RequestBody List<Long> nuevosEquiposIds) {
        try {
            PersonaEquipoDTO personaDTO = personaService.actualizarEquiposDeUsuario(idPersona, nuevosEquiposIds);
            return ResponseEntity.ok(personaDTO);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.EQUIPO_NO_ENCONTRADO, idPersona, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.EQUIPO_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.EQUIPO_ESTADO_INVALIDO, "No se pudo acceder equipo: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    // ========== TITULO ENDPOINTS ==========

    @GetMapping("/{id}/titulos")
    @Operation(summary = "Obtener títulos de una persona", description = "Lista todos los títulos académicos asociados a una persona.")
    public ResponseEntity<List<TitulosFormacionAcademicaDTO>> obtenerTitulosDePersona(@PathVariable Long id) {
        try {
            List<TitulosFormacionAcademicaDTO> titulos = personaService.obtenerTituloDeUsuario(id);
            return ResponseEntity.ok(titulos);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.TITULO_PERSONA_NO_ENCONTRADO, id, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.TITULO_PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.TITULO_PERSONA_ESTADO_INVALIDO, "No se pudo acceder a titulo: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @GetMapping("/titulo/{idTitulo}/usuarios")
    @Operation(summary = "Obtener personas por título", description = "Devuelve las personas que tienen un título académico específico.")
    public ResponseEntity<List<PersonaTituloDTO>> obtenerPersonasPorTitulo(@PathVariable Long idTitulo) {
        try {
            List<PersonaTituloDTO> personas = personaService.obtenerUsuariosPorTitulo(idTitulo);
            return ResponseEntity.ok(personas);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.PERSONA_NO_ENCONTRADA, idTitulo, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.PERSONA_ESTADO_INVALIDO, "No se pudo crear persona: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @PostMapping("/{idPersona}/titulo/{idTitulo}")
    @Operation(summary = "Asignar título a persona", description = "Permite asignar un título académico a una persona.")
    public ResponseEntity<?> asignarTituloAPersona(
            @PathVariable Long idPersona,
            @PathVariable Long idTitulo) {
        Persona persona = personaService.asignarTituloAUsuario(idPersona, idTitulo);
        return ResponseEntity.ok("Título asignado correctamente a la persona");
    }

    @PostMapping("/titulo/{idTitulo}/persona/{idPersona}")
    @Operation(summary = "Asignar persona a título", description = "Agrega una persona a la lista de personas que poseen un título académico.")
    public ResponseEntity<TitulosFormacionAcademicaDTO> agregarPersonaATitulo(@PathVariable Long idTitulo, @PathVariable Long idPersona) {
        try {
            TitulosFormacionAcademicaDTO dto = personaService.agregarUsuarioATitulo(idTitulo, idPersona);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.PERSONA_NO_ENCONTRADA, idTitulo, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.PERSONA_ESTADO_INVALIDO, "No se pudo crear persona: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @PutMapping("/titulo/{idTitulo}")
    @Operation(summary = "Actualizar personas de un título", description = "Reemplaza la lista de personas que tienen un título específico.")
    public ResponseEntity<TitulosFormacionAcademicaDTO> actualizarPersonasDeTitulo(
            @PathVariable Long idTitulo,
            @RequestBody List<Long> nuevasPersonasIds) {
        try {
            TitulosFormacionAcademicaDTO dto = personaService.actualizarUsuariosDeTitulo(idTitulo, nuevasPersonasIds);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.TITULO_PERSONA_NO_ENCONTRADO, idTitulo, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.TITULO_PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.TITULO_PERSONA_ESTADO_INVALIDO, "No se pudo acceder a titulo: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @PutMapping("/{idPersona}/titulo")
    @Operation(summary = "Actualizar títulos de una persona", description = "Reemplaza la lista de títulos académicos de una persona.")
    public ResponseEntity<PersonaTituloDTO> actualizarTitulosDePersona(
            @PathVariable Long idPersona,
            @RequestBody List<Long> nuevosTitulosIds) {
        try {
            PersonaTituloDTO personaDTO = personaService.actualizarTitulosDeUsuario(idPersona, nuevosTitulosIds);
            return ResponseEntity.ok(personaDTO);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.TITULO_PERSONA_NO_ENCONTRADO, idPersona, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.TITULO_PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.TITULO_PERSONA_ESTADO_INVALIDO, "No se pudo acceder a titulo: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @DeleteMapping("/{idPersona}/titulo/{idTitulo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un título de persona", description = "Permite eliminar título de persona.")
    public void eliminarTituloDePersona(
            @PathVariable Long idPersona,
            @PathVariable Long idTitulo) {
        personaService.eliminarTituloDeUsuario(idPersona, idTitulo);
    }

    // ========== ROL ENDPOINTS ==========

    @GetMapping("/{id}/roles")
    @Operation(summary = "Obtener roles de una persona", description = "Lista todos los roles asociados a una persona.")
    public ResponseEntity<List<RolesDTO>> obtenerRolesDePersona(@PathVariable Long id) {
        try {
            List<RolesDTO> roles = personaService.obtenerRolDeUsuario(id);
            return ResponseEntity.ok(roles);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.ROL_PERSONA_NO_ENCONTRADO, id, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.ROL_PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.ROL_PERSONA_ESTADO_INVALIDO, "No se pudo acceder a rol: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @GetMapping("/rol/{id}/usuarios")
    @Operation(summary = "Obtener personas de un rol", description = "Lista todas las personas asociadas a un rol.")
    public ResponseEntity<List<PersonaRolDTO>> obtenerPersonasPorRol(@PathVariable Long id) {
        try {
            List<PersonaRolDTO> personas = personaService.obtenerUsuariosPorRol(id);
            return ResponseEntity.ok(personas);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.ROL_PERSONA_NO_ENCONTRADO, id, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.ROL_PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.ROL_PERSONA_ESTADO_INVALIDO, "No se pudo acceder a rol: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @PostMapping("/{id}/rol/{idRol}")
    @Operation(summary = "Asignar rol a persona", description = "Agrega un rol existente a una persona.")
    public ResponseEntity<PersonaRolDTO> agregarRolAPersona(@PathVariable Long id, @PathVariable Long idRol) {
        try {
            PersonaRolDTO dto = personaService.agregarRolAUsuario(id, idRol);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.ROL_PERSONA_NO_ENCONTRADO, id, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.ROL_PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.ROL_PERSONA_ESTADO_INVALIDO, "No se pudo acceder a rol: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @PostMapping("/rol/{idRol}/persona/{idPersona}")
    @Operation(summary = "Asignar persona a rol", description = "Agrega una persona existente a un rol.")
    public ResponseEntity<RolesDTO> agregarPersonaARol(@PathVariable Long idRol, @PathVariable Long idPersona) {
        try {
            RolesDTO rolDTO = personaService.agregarUsuarioARol(idRol, idPersona);
            return ResponseEntity.ok(rolDTO);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.ROL_PERSONA_NO_ENCONTRADO, idRol, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.ROL_PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.ROL_PERSONA_ESTADO_INVALIDO, "No se pudo acceder a rol: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @PutMapping("/rol/{idRol}")
    @Operation(summary = "Actualizar personas de un rol", description = "Reemplaza la lista de personas asociadas a un rol.")
    public ResponseEntity<RolesDTO> actualizarPersonasDeRol(
            @PathVariable Long idRol,
            @RequestBody List<Long> nuevasPersonasIds) {
        try {
            RolesDTO rolDTO = personaService.actualizarUsuariosDeRol(idRol, nuevasPersonasIds);
            return ResponseEntity.ok(rolDTO);
        } catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(CodigoError.ROL_PERSONA_NO_ENCONTRADO, idRol, request.getMethod(), request.getRequestURI());
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(CodigoError.ROL_PERSONA_DATOS_INVALIDOS, e.getMessage(), request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException e) {
            throw new GenericConflictException(CodigoError.ROL_PERSONA_ESTADO_INVALIDO, "No se pudo acceder a rol: " + e.getMessage(), request.getMethod(), request.getRequestURI());
        }
    }

    @DeleteMapping("/{idPersona}/rol/{idRol}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un rol de persona", description = "Permite eliminar rol de persona.")
    public void eliminarRolDePersona(
            @PathVariable Long idPersona,
            @PathVariable Long idRol) {
        personaService.eliminarRolDeUsuario(idPersona, idRol);
    }

    @DeleteMapping("/{idPersona}/equipo/{idEquipo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un equipo de persona", description = "Permite eliminar equipo de persona.")
    public void eliminarEquipoDePersona(
            @PathVariable Long idPersona,
            @PathVariable Long idEquipo) {
        personaService.eliminarEquipoDeUsuario(idPersona, idEquipo);
    }

    // ========== BULK DTO ENDPOINTS ==========

    @GetMapping("/titulos")
    @Operation(summary = "Obtener personas con títulos", description = "Permite obtener las personas con sus títulos.")
    public List<PersonaTituloDTO> getPersonasConTitulos() {
        List<Persona> personas = personaService.findAllPersonas();
        return personasTituloMapper.toDTOList(personas);
    }

    @GetMapping("/roles")
    @Operation(summary = "Obtener personas con roles", description = "Permite obtener las personas con sus roles.")
    public List<PersonasRolDTO> getPersonasConRoles() {
        List<Persona> personas = personaService.findAllPersonas();
        return personasRolMapper.toDTOList(personas);
    }

    @GetMapping("/equipos")
    @Operation(summary = "Obtener personas con equipos", description = "Permite obtener las personas con sus equipos.")
    public List<PersonaEquipoDTO> getPersonasConEquipos() {
        List<Persona> personas = personaService.findAllPersonas();
        return personasEquipoMapper.toDTOList(personas);
    }
}
