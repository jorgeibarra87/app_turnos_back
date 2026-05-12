package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.CambiosTurnoDTO;
import com.turnos.enfermeria.model.dto.response.TurnoDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TurnosService {

    TurnoDTO create(TurnoDTO turnoDTO);

    TurnoDTO actualizarTurno(Long id, TurnoDTO turnoDetallesDTO);

    void eliminarTurno(Long id);

    Optional<TurnoDTO> findById(Long idTurno);

    List<TurnoDTO> findAll();

    List<TurnoDTO> obtenerTurnos();

    List<CambiosTurnoDTO> obtenerHistorialTurno(Long idTurno);

    List<CambiosTurnoDTO> obtenerCambiosPorTurno(Long idTurno);

    TurnoDTO restaurarTurno(Long idCambio);

    List<TurnoDTO> restaurarTurnosPorVersion(String version);

    List<TurnoDTO> cambiarEstadoDeTodosLosTurnos(String estadoActual, String nuevoEstado);

    List<TurnoDTO> obtenerTurnosPorCuadro(Long idCuadroTurno);

    List<TurnoDTO> obtenerTurnosPorCuadroConFiltros(Long idCuadroTurno, String estado,
                                                           LocalDate fechaDesde, LocalDate fechaHasta);

    void registrarTurnosEnHistorialAlCambiarVersion(Long idCuadroTurno, String versionAnterior, String nuevaVersion, String nuevoEstadoTurnos);
}
