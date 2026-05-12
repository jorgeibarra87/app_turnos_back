package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.ProcesosDTO;

import java.util.List;
import java.util.Optional;

public interface ProcesosService {

    ProcesosDTO create(ProcesosDTO procesosDTO);

    ProcesosDTO update(ProcesosDTO detalleProcesosDTO, Long id);

    Optional<ProcesosDTO> findById(Long idProceso);

    List<ProcesosDTO> findAll();

    void delete(Long idProceso);

}
