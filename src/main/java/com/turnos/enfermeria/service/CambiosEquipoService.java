package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.CambiosEquipoDTO;
import com.turnos.enfermeria.model.dto.response.CambiosPersonaEquipoDTO;
import com.turnos.enfermeria.model.entity.Equipo;
import com.turnos.enfermeria.model.entity.Persona;

import java.util.List;
import java.util.Map;

public interface CambiosEquipoService {

    void registrarCambioEquipo(Equipo equipoAnterior, Equipo equipoNuevo, String tipoCambio);

    void registrarCambioPersonaEquipo(Persona persona, Equipo equipoAnterior,
                                     Equipo equipoNuevo, String tipoCambio);

    List<CambiosEquipoDTO> obtenerHistorialEquipo(Long idEquipo);

    List<CambiosPersonaEquipoDTO> obtenerHistorialPersona(Long idPersona);

    Map<String, Object> obtenerHistorialPorCuadro(Long cuadroId);
}
