package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.TipoJornadaDTO;
import com.turnos.enfermeria.model.entity.TipoJornada;

import java.util.List;
import java.util.Optional;

public interface TipoJornadaService {
    List<TipoJornadaDTO> findAll();
    List<TipoJornadaDTO> findActivos();
    List<TipoJornadaDTO> findTrabajo();
    List<TipoJornadaDTO> findDescanso();
    Optional<TipoJornadaDTO> findById(String codigo);
    TipoJornadaDTO save(TipoJornadaDTO dto);
    TipoJornadaDTO update(String codigo, TipoJornadaDTO dto);
    void delete(String codigo);
}
