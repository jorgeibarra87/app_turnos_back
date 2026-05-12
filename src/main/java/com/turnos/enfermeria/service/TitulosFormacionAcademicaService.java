package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.TitulosFormacionAcademicaDTO;
import java.util.List;
import java.util.Optional;

public interface TitulosFormacionAcademicaService {

    TitulosFormacionAcademicaDTO create(TitulosFormacionAcademicaDTO titulosFormacionAcademicaDTO);

    TitulosFormacionAcademicaDTO update(TitulosFormacionAcademicaDTO detalleTitulosFormacionAcademicaDTO, Long id);

    Optional<TitulosFormacionAcademicaDTO> findById(Long idTitulo);

    List<TitulosFormacionAcademicaDTO> findAll();

    void delete(Long idTitulo);
}
