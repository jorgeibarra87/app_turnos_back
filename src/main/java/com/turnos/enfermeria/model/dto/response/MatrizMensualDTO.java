package com.turnos.enfermeria.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatrizMensualDTO {
    private Long idCuadroTurno;
    private String nombreCuadro;
    private String anio;
    private String mes;
    private String entidad;
    private String tipoPersonal;
    private int diasDelMes;
    private List<FilaMatriz> filas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilaMatriz {
        private Long idPersona;
        private String nombrePersona;
        private String documento;
        private String perfil;
        private List<CeldaMatrizDTO> celdas;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CeldaMatrizDTO {
        private Integer diaMes;
        private String codigoJornada;
        private String nombreJornada;
        private String color;
        private Boolean esDescanso;
        private String observacion;
    }
}
