package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.response.PersonaRolDTO;
import com.turnos.enfermeria.model.dto.response.RolesDTO;
import com.turnos.enfermeria.model.entity.Persona;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonaRolMapper {

    public PersonaRolDTO toDTO(Persona persona) {
        PersonaRolDTO dto = new PersonaRolDTO();
        dto.setIdPersona(persona.getIdPersona());
        dto.setNombreCompleto(persona.getNombreCompleto());

        List<RolesDTO> rolesDTO = persona.getRoles().stream().map(rol -> {
            RolesDTO t = new RolesDTO();
            t.setIdRol(rol.getIdRol());
            t.setRol(rol.getRol());
            t.setDescripcion(rol.getDescripcion());
            return t;
        }).collect(Collectors.toList());

        dto.setRoles(rolesDTO);
        return dto;
    }
}
