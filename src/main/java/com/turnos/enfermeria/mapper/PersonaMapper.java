package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.response.PersonaDTO;
import com.turnos.enfermeria.model.entity.Persona;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonaMapper {

    public PersonaDTO toDTO(Persona persona) {
        PersonaDTO dto = new PersonaDTO();
        dto.setIdPersona(persona.getIdPersona());
        dto.setNombreCompleto(persona.getNombreCompleto());
        dto.setDocumento(persona.getDocumento());
        dto.setEmail(persona.getEmail());
        dto.setTelefono(persona.getTelefono());
        return dto;
    }

    public List<PersonaDTO> toDTOList(List<Persona> personas) {
        return personas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
