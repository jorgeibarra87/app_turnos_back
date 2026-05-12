package com.turnos.enfermeria.service;

import java.util.Map;

public interface ReporteService {

    Map<String, Object> generarReporte(int anio, int mes, Long cuadroTurnoId);

}
