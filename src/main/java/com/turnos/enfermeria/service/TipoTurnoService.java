package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.TipoTurnoDTO;
import java.util.List;
import java.util.Optional;

public interface TipoTurnoService {

    TipoTurnoDTO create(TipoTurnoDTO tipoTurnoDTO);

    TipoTurnoDTO update(TipoTurnoDTO detalleTipoTurnoDTO, Long id);

    Optional<TipoTurnoDTO> findById(Long idTipoTurno);

    List<TipoTurnoDTO> findAll();

    void delete(Long idTipoTurno);
}
