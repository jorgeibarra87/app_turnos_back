package com.turnos.enfermeria.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PersonaRolDTO {
        private Long idPersona;
        private String nombreCompleto;
        private List<RolesDTO> roles;

}
