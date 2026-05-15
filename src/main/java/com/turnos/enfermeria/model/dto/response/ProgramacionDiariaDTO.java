package com.turnos.enfermeria.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramacionDiariaDTO {
    private Long idProgramacion;
    private Long idCuadroTurno;
    private Long idPersona;
    private String nombrePersona;
    private String documentoPersona;
    private Integer diaMes;
    private String codigoJornada;
    private String nombreJornada;
    private String colorJornada;
    private String observacion;
}
