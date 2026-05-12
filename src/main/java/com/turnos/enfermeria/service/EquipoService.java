package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.EquipoDTO;
import com.turnos.enfermeria.model.dto.request.EquipoSelectionDTO;
import com.turnos.enfermeria.model.dto.response.MiembroPerfilDTO;
import com.turnos.enfermeria.model.entity.Equipo;
import java.util.List;
import java.util.Optional;

public interface EquipoService {
    EquipoDTO create(EquipoDTO equipoDTO);
    EquipoDTO create(EquipoDTO equipoDTO, EquipoSelectionDTO selection);
    EquipoDTO update(EquipoDTO detalleEquipoDTO, Long id);
    EquipoDTO update(EquipoDTO detalleEquipoDTO, Long id, EquipoSelectionDTO selection);
    Optional<EquipoDTO> findById(Long idEquipo);
    List<EquipoDTO> findAll();
    void delete(Long idEquipo);
    EquipoDTO createWithGeneratedName(EquipoSelectionDTO selection);
    EquipoDTO updateWithGeneratedName(Long idEquipo, EquipoSelectionDTO selection);
    String generateEquipoName(EquipoSelectionDTO selection);
    Equipo createEquipoWithGeneratedName(EquipoSelectionDTO selection);
    List<MiembroPerfilDTO> obtenerMiembrosConPerfil(Long equipoId);
}
