package com.turnos.enfermeria.model.dto.response;

import lombok.Data;

@Data
public class PersonasEquipoDTO {
    private Long idPersona;
    private String nombreCompleto;
    private String documento;
}
