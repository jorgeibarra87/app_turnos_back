package com.turnos.enfermeria.model.dto.response;

import lombok.Data;

@Data
public class EntidadDTO {
    private Long idEntidad;
    private String nombre;
    private String sigla;
    private Boolean estado = true;
}
