package com.turnos.enfermeria.model.dto.response;

import lombok.Data;

@Data
public class TipoPersonalDTO {
    private Long idTipoPersonal;
    private String nombre;
    private String sigla;
    private Boolean estado = true;
}
