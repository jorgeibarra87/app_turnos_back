package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.TipoFormacionAcademicaDTO;
import java.util.List;
import java.util.Optional;

public interface TipoFormacionAcademicaService {

    TipoFormacionAcademicaDTO create(TipoFormacionAcademicaDTO tipoFormacionAcademicaDTO);

    TipoFormacionAcademicaDTO update(TipoFormacionAcademicaDTO detalleTipoFormacionAcademicaDTO, Long id);

    Optional<TipoFormacionAcademicaDTO> findById(Long idTipoFormacionAcademica);

    List<TipoFormacionAcademicaDTO> findAll();

    void delete(Long idTipoFormacionAcademica);
}
