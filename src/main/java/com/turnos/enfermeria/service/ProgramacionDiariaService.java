package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.request.ProgramacionDiariaRequest;
import com.turnos.enfermeria.model.dto.response.MatrizMensualDTO;

import java.util.List;

public interface ProgramacionDiariaService {
    MatrizMensualDTO obtenerMatrizPorCuadro(Long idCuadroTurno);
    MatrizMensualDTO guardarMatrizCompleta(ProgramacionDiariaRequest request);
    void actualizarCelda(Long idCuadroTurno, Long idPersona, Integer diaMes, String codigoJornada, String observacion);
    void eliminarMatriz(Long idCuadroTurno);
    List<MatrizMensualDTO.FilaMatriz> generarTurnosDesdeMatriz(Long idCuadroTurno);
}
