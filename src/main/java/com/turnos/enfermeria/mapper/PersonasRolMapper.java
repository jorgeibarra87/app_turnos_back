package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.response.PersonasRolDTO;
import com.turnos.enfermeria.model.dto.response.RolesDTO;
import com.turnos.enfermeria.model.entity.Persona;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonasRolMapper {

    public PersonasRolDTO toDTO(Persona persona) {
        PersonasRolDTO dto = new PersonasRolDTO();
        dto.setIdPersona(persona.getIdPersona());
        dto.setNombreCompleto(persona.getNombreCompleto());
        dto.setDocumento(persona.getDocumento());

        List<RolesDTO> rolesDTO = persona.getRoles().stream().map(rol -> {
            RolesDTO t = new RolesDTO();
            t.setIdRol(rol.getIdRol());
            t.setRol(rol.getRol());
            return t;
        }).collect(Collectors.toList());

        dto.setRoles(rolesDTO);

        return dto;
    }

    public List<PersonasRolDTO> toDTOList(List<Persona> personas) {
        return personas.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
