package com.turnos.enfermeria.service;

import org.springframework.core.io.InputStreamResource;

public interface ReporteExcelService {
    InputStreamResource exportarExcel(int anio, int mes, Long cuadroTurnoId) throws Exception;
}
