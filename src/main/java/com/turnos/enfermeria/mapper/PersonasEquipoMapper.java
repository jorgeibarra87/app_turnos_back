package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.response.PersonaEquipoDTO;
import com.turnos.enfermeria.model.entity.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonasEquipoMapper {

    @Autowired
    private EquipoMapper equipoMapper;

    public PersonaEquipoDTO toDTO(Persona persona) {
        PersonaEquipoDTO dto = new PersonaEquipoDTO();
        dto.setIdPersona(persona.getIdPersona());
        dto.setNombreCompleto(persona.getNombreCompleto());
        dto.setDocumento(persona.getDocumento());
        if (persona.getEquipos() != null) {
            dto.setEquipos(persona.getEquipos().stream()
                    .map(equipoMapper::toDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setEquipos(null);
        }
        return dto;
    }

    public List<PersonaEquipoDTO> toDTOList(List<Persona> personas) {
        return personas.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
