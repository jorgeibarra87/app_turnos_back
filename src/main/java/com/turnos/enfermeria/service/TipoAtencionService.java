package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.TipoAtencionDTO;
import java.util.List;
import java.util.Optional;

public interface TipoAtencionService {

    TipoAtencionDTO create(TipoAtencionDTO tipoAtencionDTO);

    TipoAtencionDTO update(TipoAtencionDTO detalleTipoAtencionDTO, Long id);

    Optional<TipoAtencionDTO> findById(Long idTipoAtencion);

    List<TipoAtencionDTO> findAll();

    void delete(Long idTipoAtencion);
}
