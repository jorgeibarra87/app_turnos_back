package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.RolesDTO;

import java.util.List;
import java.util.Optional;

public interface RolesService {

    RolesDTO create(RolesDTO rolesDTO);

    RolesDTO update(RolesDTO detalleRolesDTO, Long id);

    Optional<RolesDTO> findById(Long id);

    List<RolesDTO> findAll();

    void delete(Long id);

}
