package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.MatrizMensualDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FullImportService {
    Map<String, Object> importarCompleto(
            MultipartFile file,
            String anio,
            String mes,
            String entidad,
            String tipoPersonal,
            Long idEquipo,
            String categoria,
            Long idMacroproceso,
            Long idProceso,
            Long idServicio,
            Long idSeccionServicio,
            Long idSubseccionServicio,
            String observaciones
    ) throws Exception;
}
