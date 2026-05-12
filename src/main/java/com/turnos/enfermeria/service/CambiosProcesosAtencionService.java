package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.CambiosProcesosAtencionDTO;

import java.util.List;
import java.util.Optional;

public interface CambiosProcesosAtencionService {

    CambiosProcesosAtencionDTO create(CambiosProcesosAtencionDTO cambiosProcesosAtencionDTO);

    CambiosProcesosAtencionDTO update(CambiosProcesosAtencionDTO detalleCambiosProcesosAtencionDTO, Long id);

    Optional<CambiosProcesosAtencionDTO> findById(Long idCambioProcesoAtencion);

    List<CambiosProcesosAtencionDTO> findAll();

    void delete(Long idCambioProcesoAtencion);
}
