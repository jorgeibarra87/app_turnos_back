package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.CambiosTurnoDTO;

import java.util.List;
import java.util.Optional;

public interface CambiosTurnoService {

    CambiosTurnoDTO create(CambiosTurnoDTO cambiosTurnoDTO);

    CambiosTurnoDTO update(CambiosTurnoDTO detalleCambiosTurnoDTO, Long id);

    Optional<CambiosTurnoDTO> findById(Long idCambio);

    List<CambiosTurnoDTO> findAll();

    void delete(Long idCambio);

    List<CambiosTurnoDTO> obtenerCambiosPorTurno(Long idTurno);
}
