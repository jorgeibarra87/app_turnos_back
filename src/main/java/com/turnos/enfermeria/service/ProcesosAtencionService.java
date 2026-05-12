package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.ProcesosAtencionDTO;

import java.util.List;
import java.util.Optional;

public interface ProcesosAtencionService {

    ProcesosAtencionDTO create(ProcesosAtencionDTO procesosAtencionDTO);

    ProcesosAtencionDTO update(ProcesosAtencionDTO detalleProcesosAtencionDTO, Long id);

    Optional<ProcesosAtencionDTO> findById(Long idProcesoAtencion);

    List<ProcesosAtencionDTO> findAll();

    List<ProcesosAtencionDTO> findByCuadro(Long idCuadro);

}
