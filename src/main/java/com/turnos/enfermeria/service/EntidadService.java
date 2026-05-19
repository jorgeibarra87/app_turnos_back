package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.EntidadDTO;

import java.util.List;
import java.util.Optional;

public interface EntidadService {

    EntidadDTO create(EntidadDTO entidadDTO);

    EntidadDTO update(EntidadDTO entidadDTO, Long id);

    Optional<EntidadDTO> findById(Long id);

    List<EntidadDTO> findAll();

    void delete(Long id);
}
