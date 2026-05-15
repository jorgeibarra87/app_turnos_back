package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.MatrizMensualDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelImportService {
    MatrizMensualDTO importarExcel(MultipartFile file, Long idCuadroTurno) throws Exception;
    String validarEstructuraExcel(MultipartFile file) throws Exception;
}
