package com.turnos.enfermeria.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PersonasRolDTO {
    private Long idPersona;
    private String nombreCompleto;
    private String documento;
    private List<RolesDTO> roles;
}
