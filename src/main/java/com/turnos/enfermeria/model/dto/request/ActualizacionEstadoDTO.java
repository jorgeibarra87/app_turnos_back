package com.turnos.enfermeria.model.dto.request;

import lombok.Data;

@Data
public class ActualizacionEstadoDTO {
    private Long idNotificacion;
    private Boolean estado;
    private String estadoNotificacion;
}