package com.turnos.enfermeria.model.dto.response;

import lombok.Data;

@Data
public class TitulosFormacionAcademicaDTO {
    private Long idTitulo;
    private String titulo;
    private Long idTipoFormacionAcademica;
    private Boolean estado = true;
    private String nombreTipo;
}
