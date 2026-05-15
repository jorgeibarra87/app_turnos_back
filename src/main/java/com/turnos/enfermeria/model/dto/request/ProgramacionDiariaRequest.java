package com.turnos.enfermeria.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramacionDiariaRequest {
    private Long idCuadroTurno;
    private List<CeldaMatriz> celdas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CeldaMatriz {
        private Long idPersona;
        private Integer diaMes;
        private String codigoJornada;
        private String observacion;
    }
}
