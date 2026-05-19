package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.TipoPersonalDTO;

import java.util.List;
import java.util.Optional;

public interface TipoPersonalService {

    TipoPersonalDTO create(TipoPersonalDTO tipoPersonalDTO);

    TipoPersonalDTO update(TipoPersonalDTO tipoPersonalDTO, Long id);

    Optional<TipoPersonalDTO> findById(Long id);

    List<TipoPersonalDTO> findAll();

    void delete(Long id);
}
