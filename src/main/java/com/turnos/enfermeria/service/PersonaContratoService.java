package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.PersonaContratoDTO;
import com.turnos.enfermeria.model.dto.response.PersonaContratoTotalDTO;
import java.util.List;
import java.util.Optional;

public interface PersonaContratoService {
    PersonaContratoDTO create(PersonaContratoDTO personaContratoDTO);
    PersonaContratoDTO update(PersonaContratoDTO detallePersonaContratoDTO, Long id);
    Optional<PersonaContratoDTO> findById(Long idPersonaContrato);
    List<PersonaContratoDTO> findAll();
    void delete(Long idPersonaContrato);
    PersonaContratoTotalDTO obtenerInformacionPersonaCompleta(String documento);
    PersonaContratoTotalDTO savePersonaContratoCompleto(PersonaContratoTotalDTO personaContratoTotalDTO);
    PersonaContratoTotalDTO updatePersonaContratoCompleto(String documento, PersonaContratoTotalDTO personaContratoTotalDTO);
}
