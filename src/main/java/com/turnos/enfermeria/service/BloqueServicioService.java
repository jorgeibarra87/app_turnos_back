package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.BloqueServicioDTO;

import java.util.List;
import java.util.Optional;

public interface BloqueServicioService {

    BloqueServicioDTO create(BloqueServicioDTO bloqueServicioDTO);

    BloqueServicioDTO update(BloqueServicioDTO detalleBloqueServicioDTO, Long id);

    Optional<BloqueServicioDTO> findById(Long idBloqueServicio);

    List<BloqueServicioDTO> findAll();

    void delete(Long idBloqueServicio);
}
