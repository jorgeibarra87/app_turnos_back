package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.ServicioDTO;

import java.util.List;
import java.util.Optional;

public interface ServicioService {

    ServicioDTO create(ServicioDTO servicioDTO);

    ServicioDTO update(ServicioDTO detalleServicioDTO, Long id);

    Optional<ServicioDTO> findById(Long idServicio);

    List<ServicioDTO> findAll();

    void delete(Long idServicio);

}
