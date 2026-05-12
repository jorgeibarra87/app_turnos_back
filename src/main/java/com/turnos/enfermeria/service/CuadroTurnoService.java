package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.*;
import com.turnos.enfermeria.model.dto.request.CuadroTurnoRequest;
import com.turnos.enfermeria.model.entity.CuadroTurno;

import java.util.List;
import java.util.Optional;

public interface CuadroTurnoService {

    CuadroTurnoDTO crearCuadroTurno(CuadroTurnoDTO cuadroTurnoDTO);

    List<CuadroTurnoDTO> obtenerCuadrosTurno();

    Optional<CuadroTurnoDTO> findById(Long idCuadroTurno);

    List<CambiosCuadroTurnoDTO> obtenerHistorialCuadroTurno(Long id);

    List<CambiosTurnoDTO> obtenerHistorialTurnos(Long id);

    CuadroTurnoDTO actualizarCuadroTurno(Long id, CuadroTurnoDTO cuadroTurnoDTO);

    void manejarVersionesPorEstado(CuadroTurno cuadro, String estadoAnterior, String nuevoEstado);

    void eliminarCuadroTurno(Long id);

    CuadroTurnoDTO restaurarCuadroTurno(Long idCambio);

    CuadroTurnoDTO restaurarCuadroYTurnosAVersion(Long idCuadroTurno, String versionDeseada);

    CambiosEstadoDTO cambiarEstadoDeCuadrosYTurnos(String estadoActual, String nuevoEstado, List<Long> idsCuadros);

    CuadroTurnoDTO actualizarTurnoExcepcion(Long id, Boolean nuevoValor, String tipoCambio);

    CuadroTurnoDTO crearCuadroTurnoTotal(CuadroTurnoRequest request);

    CuadroTurnoDTO editarCuadroTurnoTotal(Long idCuadro, CuadroTurnoRequest request);

    String obtenerVistaPreviaNombre(CuadroTurnoRequest request);

    boolean existeCuadroTurnoSimilar(CuadroTurnoRequest request);

    List<ProcesosDTO> obtenerProcesosDesdeCuadroMultiproceso(Long idCuadroTurno);
}
