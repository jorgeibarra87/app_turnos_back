package com.turnos.enfermeria.service;

import org.springframework.core.io.InputStreamResource;

public interface PlantillaExcelService {
    InputStreamResource descargarPlantilla(int diasDelMes) throws Exception;
}
