package com.turnos.enfermeria.controller;

import com.turnos.enfermeria.exception.CodigoError;
import com.turnos.enfermeria.exception.custom.GenericBadRequestException;
import com.turnos.enfermeria.exception.custom.GenericConflictException;
import com.turnos.enfermeria.exception.custom.GenericNotFoundException;
import com.turnos.enfermeria.model.dto.response.SubseccionesServicioDTO;
import com.turnos.enfermeria.service.SubseccionesServicioService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;

@Validated
@RestController
@RequestMapping("/subseccionesServicio")
//@Tag(name = "Subsecciones de Servicio", description = "Operaciones relacionadas con las subsecciones de los servicios")
@AllArgsConstructor
public class SubseccionesServicioController {

    private final SubseccionesServicioService subseccionesServicioService;
    private final HttpServletRequest request;

    @PostMapping
    @Operation(summary = "Crear subsección de servicio", description = "Crea una nueva subsección asociada a un servicio",
            tags={"Servicios"})
    public ResponseEntity<SubseccionesServicioDTO> create(@RequestBody SubseccionesServicioDTO subseccionesServicioDTO){
        try {
            SubseccionesServicioDTO nuevoSubseccionesServicioDTO = subseccionesServicioService.create(subseccionesServicioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoSubseccionesServicioDTO);
        }catch (EntityNotFoundException e) {
            throw new GenericNotFoundException(
                    CodigoError.SUBSECCION_SERVICIO_NO_ENCONTRADO,
                    subseccionesServicioDTO.getIdSubseccionServicio(),
                    request.getMethod(),
                    request.getRequestURI()
            );
        } catch (IllegalArgumentException e) {
            throw new GenericBadRequestException(
                    CodigoError.SUBSECCION_SERVICIO_DATOS_INVALIDOS,
                    e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        } catch (IllegalStateException e) {
            throw new GenericConflictException(
                    CodigoError.SUBSECCION_SERVICIO_ESTADO_INVALIDO,
                    "No se pudo crear subseccion: " + e.getMessage(),
                    request.getMethod(),
                    request.getRequestURI()
            );

        }
    }

    @GetMapping
    @Operation(summary = "Listar todas las subsecciones", description = "Devuelve todas las subsecciones de servicio registradas",
            tags={"Servicios"})
    public ResponseEntity<List<SubseccionesServicioDTO>> findAll(){
        List<SubseccionesServicioDTO> subseccionesServicioDTO = subseccionesServicioService.findAll();
        return subseccionesServicioDTO.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(subseccionesServicioDTO);
    }

    @GetMapping("/{idSubseccionServicio}")
    @Operation(summary = "Buscar subsección por ID", description = "Devuelve los datos de una subsección específica por su ID",
            tags={"Servicios"})
    public ResponseEntity<SubseccionesServicioDTO> findById(@PathVariable Long idSubseccionServicio){
        return subseccionesServicioService.findById(idSubseccionServicio)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.SUBSECCION_SERVICIO_NO_ENCONTRADO,
                        idSubseccionServicio,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }

    @PutMapping("/{idSubseccionServicio}")
    @Operation(summary = "Actualizar subsección", description = "Actualiza los datos de una subsección existente",
            tags={"Servicios"})
    public ResponseEntity<SubseccionesServicioDTO> update(@RequestBody SubseccionesServicioDTO subseccionesServicioDTO, @PathVariable("idSubseccionServicio") Long idSubseccionServicio){
        return subseccionesServicioService.findById(idSubseccionServicio)
                .map(subseccionesServicioExistente -> ResponseEntity.ok(subseccionesServicioService.update(subseccionesServicioDTO, idSubseccionServicio)))
                .orElseThrow(() -> new GenericNotFoundException(
                        CodigoError.SUBSECCION_SERVICIO_NO_ENCONTRADO,
                        idSubseccionServicio,
                        request.getMethod(),
                        request.getRequestURI()
                ));
    }
//    @DeleteMapping("/{idSubseccionServicio}")
//    @Operation(summary = "Eliminar subsección", description = "Elimina una subsección existente por su ID",
//            tags={"Servicios"})
//    public ResponseEntity<Object> delete(@PathVariable Long idSubseccionServicio){
//        return subseccionesServicioService.findById(idSubseccionServicio)
//                .map(subseccionesServicioDTO-> {
//                    subseccionesServicioService.delete(idSubseccionServicio);
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseThrow(() -> new GenericNotFoundException(
//                        CodigoError.SUBSECCION_SERVICIO_NO_ENCONTRADO,
//                        idSubseccionServicio,
//                        request.getMethod(),
//                        request.getRequestURI()
//                ));
//    }
}
