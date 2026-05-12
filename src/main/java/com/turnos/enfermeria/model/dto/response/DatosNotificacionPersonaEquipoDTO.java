package com.turnos.enfermeria.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DatosNotificacionPersonaEquipoDTO {
    private PersonaDTO persona;
    private EquipoDTO equipo;
    private List<EquipoDTO> equiposAnteriores;
    private List<CambiosPersonaEquipoDTO> historialCambios;
}
