package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.MacroprocesosDTO;
import com.turnos.enfermeria.model.entity.Macroprocesos;
import java.util.List;
import java.util.Optional;

public interface MacroprocesosService {
    Macroprocesos create(Macroprocesos macroprocesos);
    MacroprocesosDTO create(MacroprocesosDTO macroprocesosDTO);
    MacroprocesosDTO update(MacroprocesosDTO detalleMacroprocesosDTO, Long id);
    Optional<MacroprocesosDTO> findById(Long idMacroproceso);
    List<MacroprocesosDTO> findAll();
    void delete(Long idMacroproceso);
}
