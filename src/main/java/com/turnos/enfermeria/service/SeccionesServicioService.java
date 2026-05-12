package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.SeccionesServicioDTO;

import java.util.List;
import java.util.Optional;

public interface SeccionesServicioService {

    SeccionesServicioDTO create(SeccionesServicioDTO seccionesServicioDTO);

    SeccionesServicioDTO update(SeccionesServicioDTO detalleSeccionesServicioDTO, Long id);

    Optional<SeccionesServicioDTO> findById(Long idSeccionServicio);

    List<SeccionesServicioDTO> findAll();

    void delete(Long idSeccionServicio);

}
