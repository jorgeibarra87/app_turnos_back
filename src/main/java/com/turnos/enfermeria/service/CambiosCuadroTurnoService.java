package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.CambiosCuadroTurnoDTO;
import com.turnos.enfermeria.model.dto.response.CuadroTurnoDTO;
import com.turnos.enfermeria.model.entity.ProcesosAtencion;

import java.util.List;
import java.util.Optional;

public interface CambiosCuadroTurnoService {

    CambiosCuadroTurnoDTO create(CambiosCuadroTurnoDTO cambiosCuadroTurnoDTO);

    CambiosCuadroTurnoDTO update(CambiosCuadroTurnoDTO cambiosCuadroTurnoDTO, Long id);

    Optional<CambiosCuadroTurnoDTO> findById(Long idCambioCuadro);

    List<CambiosCuadroTurnoDTO> findAll();

    void delete(Long idCambioCuadro);

    void registrarCambioCuadroTurno(CuadroTurnoDTO cuadroTurnoDTO, String tipoCambio);

    void registrarCambioProcesosAtencion(ProcesosAtencion procesosAtencion, String tipoCambio);
}
