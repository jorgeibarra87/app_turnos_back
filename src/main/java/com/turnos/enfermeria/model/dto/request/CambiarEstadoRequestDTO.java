package com.turnos.enfermeria.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CambiarEstadoRequestDTO {
    private String estadoActual;
    private String nuevoEstado;
    private List<Long> idsCuadros;
}
