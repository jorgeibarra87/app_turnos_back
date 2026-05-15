package com.turnos.enfermeria.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoJornadaDTO {
    private String codigo;
    private String nombre;
    private String horaInicio;
    private String horaFin;
    private Boolean esDescanso;
    private Boolean esTrabajo;
    private String color;
    private Integer orden;
    private Boolean estado;
}
