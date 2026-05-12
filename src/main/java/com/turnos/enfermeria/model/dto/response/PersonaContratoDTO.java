package com.turnos.enfermeria.model.dto.response;

import lombok.Data;

@Data
public class PersonaContratoDTO {
    private Long idPersonaContrato;
    private Long idPersona;
    private Long idContrato;
    private Long idRol;
    private Boolean estado = true;
}
