package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.SubseccionesServicioDTO;
import java.util.List;
import java.util.Optional;

public interface SubseccionesServicioService {

    SubseccionesServicioDTO create(SubseccionesServicioDTO subseccionesServicioDTO);

    SubseccionesServicioDTO update(SubseccionesServicioDTO detalleSubseccionesServicioDTO, Long id);

    Optional<SubseccionesServicioDTO> findById(Long idSubseccionServicio);

    List<SubseccionesServicioDTO> findAll();

    void delete(Long idSubseccionServicio);
}
