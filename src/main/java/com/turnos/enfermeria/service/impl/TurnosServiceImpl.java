package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.events.CambioTurnoEvent;
import com.turnos.enfermeria.exception.custom.TurnoValidationException;
import com.turnos.enfermeria.model.dto.response.CambiosTurnoDTO;
import com.turnos.enfermeria.model.dto.response.TurnoDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import com.turnos.enfermeria.service.TurnosService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class TurnosServiceImpl implements TurnosService {

    private final TurnosRepository turnosRepository;
    private final ContratoRepository contratoRepository;
    private final TipoTurnoRepository tipoTurnoRepository;
    private final PersonaRepository personaRepository;
    private final CuadroTurnoRepository cuadroTurnoRepository;
    private final CambiosTurnoRepository cambiosTurnoRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TurnoDTO create(TurnoDTO turnoDTO) {
        Persona persona = buscarPersona(turnoDTO.getIdPersona());
        CuadroTurno cuadroTurno = buscarCuadroTurno(turnoDTO.getIdCuadroTurno());

        validarDatosTurno(turnoDTO);
        validarSolapamientoTurnos(turnoDTO);
        validarReglasNegocio(turnoDTO, persona, cuadroTurno);

        Turnos turno = modelMapper.map(turnoDTO, Turnos.class);
        configurarTurno(turno, turnoDTO, persona, cuadroTurno);

        Turnos turnoGuardado = turnosRepository.save(turno);

        TurnoDTO turnoGuardadoDTO = modelMapper.map(turnoGuardado, TurnoDTO.class);
        registrarCambioTurno(turnoGuardado.getIdTurno(), turnoGuardadoDTO, "CREACION");

        try {
            CambioTurnoEvent evento = new CambioTurnoEvent(
                    turnoGuardado.getIdTurno(),
                    "CREACIÓN DE TURNO",
                    "Se ha creado un nuevo turno para " + persona.getNombreCompleto() +
                            " del " + turnoGuardado.getFechaInicio() + " al " + turnoGuardado.getFechaFin() +
                            " (" + turnoGuardado.getTipoTurno() + ")",
                    cuadroTurno.getIdCuadroTurno().toString()
            );
            eventPublisher.publishEvent(evento);
            log.info("Evento de creación publicado para turno ID: {}", turnoGuardado.getIdTurno());
        } catch (Exception eventException) {
            log.error("Error al publicar evento de creación de turno: {}", eventException.getMessage());
        }

        return turnoGuardadoDTO;
    }

    public TurnoDTO actualizarTurno(Long id, TurnoDTO turnoDetallesDTO) {
        Turnos turnoExistente = buscarTurno(id);

        TurnoDTO estadoAnterior = modelMapper.map(turnoExistente, TurnoDTO.class);
        registrarCambioTurno(id, estadoAnterior, "ACTUALIZACION_ANTERIOR");

        actualizarCamposTurno(turnoExistente, turnoDetallesDTO);

        turnoExistente.setTotalHoras(calcularHorasTrabajadas(
                turnoExistente.getFechaInicio(), turnoExistente.getFechaFin()));
        turnoExistente.setVersion(generarNuevaVersion(turnoExistente.getVersion()));

        Turnos turnoActualizado = turnosRepository.save(turnoExistente);

        TurnoDTO estadoNuevo = modelMapper.map(turnoActualizado, TurnoDTO.class);
        registrarCambioTurno(id, estadoNuevo, "ACTUALIZACION");

        try {
            Persona persona = buscarPersona(turnoDetallesDTO.getIdPersona());
            CambioTurnoEvent evento = new CambioTurnoEvent(
                    turnoActualizado.getIdTurno(),
                    "MODIFICACIÓN DE TURNO",
                    "Se ha modificado el turno de " + persona.getNombreCompleto() +
                            " - Nuevas fechas: " + turnoActualizado.getFechaInicio() +
                            " al " + turnoActualizado.getFechaFin(),
                    turnoActualizado.getCuadroTurno().getIdCuadroTurno().toString()
            );
            eventPublisher.publishEvent(evento);
            log.info("Evento de modificación publicado para turno ID: {}", turnoActualizado.getIdTurno());
        } catch (Exception eventException) {
            log.error("Error al publicar evento de modificación de turno: {}", eventException.getMessage());
        }

        return estadoNuevo;
    }

    public void eliminarTurno(Long id) {
        Turnos turnoEliminar = buscarTurno(id);
        TurnoDTO turnoDTO = modelMapper.map(turnoEliminar, TurnoDTO.class);
        registrarCambioTurno(id, turnoDTO, "ELIMINACION");
        turnosRepository.deleteById(id);
    }

    public Optional<TurnoDTO> findById(Long idTurno) {
        return turnosRepository.findById(idTurno)
                .map(turno -> modelMapper.map(turno, TurnoDTO.class));
    }

    public List<TurnoDTO> findAll() {
        return turnosRepository.findAll()
                .stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    public List<TurnoDTO> obtenerTurnos() {
        return findAll();
    }

    public List<CambiosTurnoDTO> obtenerHistorialTurno(Long idTurno) {
        List<CambiosTurno> cambios = cambiosTurnoRepository.findByTurno_IdTurno(idTurno);
        return cambios.stream()
                .map(cambio -> modelMapper.map(cambio, CambiosTurnoDTO.class))
                .collect(Collectors.toList());
    }

    public List<CambiosTurnoDTO> obtenerCambiosPorTurno(Long idTurno) {
        return cambiosTurnoRepository.findCambiosTurnoDTOByTurno(idTurno);
    }

    public TurnoDTO restaurarTurno(Long idCambio) {
        CambiosTurno cambio = buscarCambioTurno(idCambio);
        Optional<Turnos> turnoOpt = turnosRepository.findById(cambio.getTurno().getIdTurno());
        Turnos turnoRestaurado = turnoOpt.orElse(new Turnos());
        restaurarDatosDesdeHistorial(turnoRestaurado, cambio);
        Turnos turnoGuardado = turnosRepository.save(turnoRestaurado);
        return modelMapper.map(turnoGuardado, TurnoDTO.class);
    }

    @Transactional
    public List<TurnoDTO> restaurarTurnosPorVersion(String version) {
        List<CambiosTurno> cambiosTurno = cambiosTurnoRepository.findByVersion(version);
        if (cambiosTurno.isEmpty()) {
            throw new RuntimeException("No se encontraron turnos con la versión " + version);
        }
        return cambiosTurno.stream()
                .map(this::restaurarTurnoDesdeHistorial)
                .collect(Collectors.toList());
    }

    public List<TurnoDTO> cambiarEstadoDeTodosLosTurnos(String estadoActual, String nuevoEstado) {
        List<Turnos> turnos = turnosRepository.findByEstadoTurno(estadoActual);
        turnos.forEach(turno -> turno.setEstadoTurno(nuevoEstado));
        List<Turnos> turnosActualizados = turnosRepository.saveAll(turnos);
        return turnosActualizados.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    public List<TurnoDTO> obtenerTurnosPorCuadro(Long idCuadroTurno) {
        buscarCuadroTurno(idCuadroTurno);
        List<Turnos> turnos = turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno);
        return turnos.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    public List<TurnoDTO> obtenerTurnosPorCuadroConFiltros(Long idCuadroTurno, String estado,
                                                           LocalDate fechaDesde, LocalDate fechaHasta) {
        buscarCuadroTurno(idCuadroTurno);
        List<Turnos> turnos = aplicarFiltrosTurnos(idCuadroTurno, estado, fechaDesde, fechaHasta);
        return turnos.stream()
                .map(turno -> modelMapper.map(turno, TurnoDTO.class))
                .collect(Collectors.toList());
    }

    private Persona buscarPersona(Long id) {
        return id != null ? personaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada.")) : null;
    }

    private CuadroTurno buscarCuadroTurno(Long id) {
        return id != null ? cuadroTurnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cuadro de turno no encontrado.")) : null;
    }

    private Turnos buscarTurno(Long id) {
        return turnosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));
    }

    private CambiosTurno buscarCambioTurno(Long id) {
        return cambiosTurnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cambio de turno no encontrado"));
    }

    private void validarDatosTurno(TurnoDTO turnoDTO) {
        if (turnoDTO.getFechaInicio() == null || turnoDTO.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas.");
        }
        if (turnoDTO.getFechaInicio().isAfter(turnoDTO.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
    }

    private void validarSolapamientoTurnos(TurnoDTO turnoDTO) {
        List<Turnos> turnosSolapados = turnosRepository.findTurnosSolapados(
                turnoDTO.getIdPersona(), turnoDTO.getFechaInicio(), turnoDTO.getFechaFin());
        if (!turnosSolapados.isEmpty()) {
            throw new TurnoValidationException(
                    String.format("Ya existe un turno en este horario para el usuario. " +
                                    "Conflicto desde %s hasta %s.",
                            turnoDTO.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            turnoDTO.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                    "POST", "/turnos"
            );
        }
    }

    private void validarReglasNegocio(TurnoDTO turnoDTO, Persona persona, CuadroTurno cuadroTurno) {
        Long usuarioId = persona.getIdPersona();
        LocalDate fechaTurno = turnoDTO.getFechaInicio().toLocalDate();
        long horasTurno = calcularHorasTrabajadas(turnoDTO.getFechaInicio(), turnoDTO.getFechaFin());

        validarHorasMensuales(usuarioId, fechaTurno, horasTurno);
        validarLimitesHorarios(usuarioId, fechaTurno, horasTurno, cuadroTurno);
    }

    private void validarHorasMensuales(Long usuarioId, LocalDate fechaTurno, long horasTurno) {
        int horasMensuales = turnosRepository.obtenerHorasMensuales(
                usuarioId,
                String.valueOf(fechaTurno.getMonthValue()),
                String.valueOf(fechaTurno.getYear())
        );
        if (horasMensuales + horasTurno > 280) {
            throw new TurnoValidationException(
                    String.format("No se pueden superar las 280 horas mensuales. " +
                                    "Horas actuales del mes: %d, horas del nuevo turno: %d, total: %d horas. " +
                                    "Máximo permitido: 280 horas.",
                            horasMensuales, horasTurno, horasMensuales + horasTurno),
                    "POST", "/turnos"
            );
        }
    }

    private void validarLimitesHorarios(Long usuarioId, LocalDate fechaTurno, long horasTurno, CuadroTurno cuadroTurno) {
        List<Turnos> turnosDelDia = turnosRepository.obtenerTurnosPorFecha(usuarioId, fechaTurno);
        long horasDiariasTotales = turnosDelDia.stream().mapToLong(Turnos::getTotalHoras).sum() + horasTurno;

        if (horasTurno == 24) {
            if (!cuadroTurno.getTurnoExcepcion()) {
                throw new TurnoValidationException(
                        String.format("El usuario no puede ser asignado a turnos de 24 horas. " +
                                "El cuadro '%s' no permite turnos de excepción.", cuadroTurno.getNombre()),
                        "POST", "/turnos"
                );
            }
        } else {
            if (horasTurno > 12) {
                throw new TurnoValidationException(
                        String.format("No se pueden asignar más de 12 horas consecutivas. " +
                                "Horas solicitadas: %d horas. Máximo permitido: 12 horas.", horasTurno),
                        "POST", "/turnos"
                );
            }
            if (horasDiariasTotales > 18) {
                throw new TurnoValidationException(
                        String.format("No se pueden superar las 18 horas diarias. " +
                                        "Horas actuales del día: %d, horas del nuevo turno: %d, total: %d horas. " +
                                        "Máximo permitido: 18 horas.",
                                horasDiariasTotales - horasTurno, horasTurno, horasDiariasTotales),
                        "POST", "/turnos"
                );
            }
        }
    }

    private void configurarTurno(Turnos turno, TurnoDTO dto, Persona persona, CuadroTurno cuadroTurno) {
        turno.setUsuario(persona);
        turno.setCuadroTurno(cuadroTurno);
        turno.setTotalHoras(calcularHorasTrabajadas(dto.getFechaInicio(), dto.getFechaFin()));
        turno.setVersion(cuadroTurno.getVersion());
        turno.setComentarios(dto.getComentarios());
        turno.setTipoTurno(dto.getTipoTurno() != null ? dto.getTipoTurno() : "Presencial");
        turno.setJornada(calcularJornadaAutomatica(dto.getFechaInicio(), dto.getFechaFin()));
        turno.setEstadoTurno(dto.getEstadoTurno() != null ? dto.getEstadoTurno() : "abierto");
        if (dto.getJornada() != null && !dto.getJornada().trim().isEmpty()) {
            turno.setJornada(dto.getJornada());
            System.out.println("⚠️ Jornada manual aplicada: " + dto.getJornada());
        }
    }

    public void registrarTurnosEnHistorialAlCambiarVersion(Long idCuadroTurno, String versionAnterior, String nuevaVersion, String nuevoEstadoTurnos) {
        try {
            List<Turnos> turnos = turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno);

            for (Turnos turno : turnos) {
                CambiosTurno cambioVersionAnterior = new CambiosTurno();
                cambioVersionAnterior.setTurno(turno);
                cambioVersionAnterior.setCuadroTurno(turno.getCuadroTurno());
                cambioVersionAnterior.setUsuario(turno.getUsuario());
                cambioVersionAnterior.setFechaCambio(LocalDateTime.now());
                cambioVersionAnterior.setFechaInicio(turno.getFechaInicio());
                cambioVersionAnterior.setFechaFin(turno.getFechaFin());
                cambioVersionAnterior.setTotalHoras(turno.getTotalHoras());
                cambioVersionAnterior.setEstadoTurno(turno.getEstadoTurno());
                cambioVersionAnterior.setVersion(versionAnterior);
                cambioVersionAnterior.setComentarios(turno.getComentarios());
                cambioVersionAnterior.setTipoTurno(turno.getTipoTurno() != null ? turno.getTipoTurno() : "Presencial");
                cambioVersionAnterior.setJornada(turno.getJornada() != null ? turno.getJornada() :
                        calcularJornadaAutomatica(turno.getFechaInicio(), turno.getFechaFin()));

                cambiosTurnoRepository.save(cambioVersionAnterior);

                turno.setVersion(nuevaVersion);
                turno.setEstadoTurno(nuevoEstadoTurnos);

                CambiosTurno cambioVersionNueva = new CambiosTurno();
                cambioVersionNueva.setTurno(turno);
                cambioVersionNueva.setCuadroTurno(turno.getCuadroTurno());
                cambioVersionNueva.setUsuario(turno.getUsuario());
                cambioVersionNueva.setFechaCambio(LocalDateTime.now());
                cambioVersionNueva.setFechaInicio(turno.getFechaInicio());
                cambioVersionNueva.setFechaFin(turno.getFechaFin());
                cambioVersionNueva.setTotalHoras(turno.getTotalHoras());
                cambioVersionNueva.setEstadoTurno(nuevoEstadoTurnos);
                cambioVersionNueva.setVersion(nuevaVersion);
                cambioVersionNueva.setComentarios(turno.getComentarios());
                cambioVersionNueva.setTipoTurno(turno.getTipoTurno() != null ? turno.getTipoTurno() : "Presencial");
                cambioVersionNueva.setJornada(turno.getJornada() != null ? turno.getJornada() :
                        calcularJornadaAutomatica(turno.getFechaInicio(), turno.getFechaFin()));

                cambiosTurnoRepository.save(cambioVersionNueva);
            }

            turnosRepository.saveAll(turnos);
            System.out.println(String.format("✅ Registrados %d turnos en historial: %s -> %s (estado: %s)",
                    turnos.size(), versionAnterior, nuevaVersion, nuevoEstadoTurnos));
        } catch (Exception e) {
            System.err.println("Error al registrar turnos en historial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarCamposTurno(Turnos turnoExistente, TurnoDTO turnoDetallesDTO) {
        boolean fechasCambiaron = false;
        if (turnoDetallesDTO.getFechaInicio() != null) {
            turnoExistente.setFechaInicio(turnoDetallesDTO.getFechaInicio());
            fechasCambiaron = true;
        }
        if (turnoDetallesDTO.getFechaFin() != null) {
            turnoExistente.setFechaFin(turnoDetallesDTO.getFechaFin());
            fechasCambiaron = true;
        }
        if (turnoDetallesDTO.getEstadoTurno() != null) {
            turnoExistente.setEstadoTurno(turnoDetallesDTO.getEstadoTurno());
        }
        if (turnoDetallesDTO.getComentarios() != null) {
            turnoExistente.setComentarios(turnoDetallesDTO.getComentarios());
        }
        if (fechasCambiaron) {
            String nuevaJornada = calcularJornadaAutomatica(
                    turnoExistente.getFechaInicio(),
                    turnoExistente.getFechaFin()
            );
            turnoExistente.setJornada(nuevaJornada);
            System.out.println("✅ Jornada recalculada automáticamente: " + nuevaJornada);
        }
    }

    private void restaurarDatosDesdeHistorial(Turnos turno, CambiosTurno cambio) {
        turno.setUsuario(cambio.getUsuario());
        turno.setFechaInicio(cambio.getFechaInicio());
        turno.setFechaFin(cambio.getFechaFin());
        turno.setEstadoTurno(cambio.getEstadoTurno());
        turno.setTotalHoras(cambio.getTotalHoras());
        turno.setVersion(generarNuevaVersion(cambio.getVersion()));
        turno.setComentarios(cambio.getComentarios());
    }

    private TurnoDTO restaurarTurnoDesdeHistorial(CambiosTurno cambio) {
        Optional<Turnos> turnoOpt = turnosRepository.findById(cambio.getTurno().getIdTurno());
        Turnos turnoRestaurado = turnoOpt.orElse(new Turnos());
        restaurarDatosDesdeHistorial(turnoRestaurado, cambio);
        turnoRestaurado.setVersion(cambio.getVersion());
        Turnos turnoGuardado = turnosRepository.save(turnoRestaurado);
        return modelMapper.map(turnoGuardado, TurnoDTO.class);
    }

    private List<Turnos> aplicarFiltrosTurnos(Long idCuadroTurno, String estado,
                                              LocalDate fechaDesde, LocalDate fechaHasta) {
        if (estado != null && fechaDesde != null && fechaHasta != null) {
            return turnosRepository.findByCuadroTurnoAndEstadoAndFechaRange(
                    idCuadroTurno, estado, fechaDesde.atStartOfDay(), fechaHasta.atTime(23, 59, 59));
        } else if (estado != null) {
            return turnosRepository.findByCuadroTurno_IdCuadroTurnoAndEstadoTurno(idCuadroTurno, estado);
        } else if (fechaDesde != null && fechaHasta != null) {
            return turnosRepository.findByCuadroTurnoAndFechaRange(
                    idCuadroTurno, fechaDesde.atStartOfDay(), fechaHasta.atTime(23, 59, 59));
        } else {
            return turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno);
        }
    }

    private void registrarCambioTurno(Long idTurno, TurnoDTO turnoDTO, String tipoCambio) {
        Turnos turnoOriginal = buscarTurno(idTurno);
        CuadroTurno cuadroTurno = turnoOriginal.getCuadroTurno();
        CambiosTurno cambio = modelMapper.map(turnoDTO, CambiosTurno.class);
        cambio.setTurno(turnoOriginal);
        cambio.setFechaCambio(LocalDateTime.now());
        cambio.setUsuario(turnoOriginal.getUsuario());
        cambio.setCuadroTurno(cuadroTurno);
        cambio.setVersion(cuadroTurno.getVersion());
        cambio.setTipoTurno(turnoOriginal.getTipoTurno() != null ? turnoOriginal.getTipoTurno() : "Presencial");
        cambio.setJornada(turnoOriginal.getJornada() != null ? turnoOriginal.getJornada() :
                calcularJornadaAutomatica(turnoOriginal.getFechaInicio(), turnoOriginal.getFechaFin()));
        cambiosTurnoRepository.save(cambio);
    }

    private long calcularHorasTrabajadas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            return 0;
        }
        return Duration.between(inicio, fin).toHours();
    }

    private String generarVersion(LocalDateTime fechaInicio) {
        return fechaInicio.format(DateTimeFormatter.ofPattern("MMyy")) + "_v1";
    }

    private String generarNuevaVersion(String versionAnterior) {
        if (versionAnterior == null || !versionAnterior.contains("_v")) {
            return generarVersion(LocalDateTime.now());
        }
        String[] partes = versionAnterior.split("_v");
        if (partes.length == 2) {
            try {
                String baseVersion = partes[0];
                int numeroVersion = Integer.parseInt(partes[1]);
                return baseVersion + "_v" + (numeroVersion + 1);
            } catch (NumberFormatException e) {
                return partes[0] + "_v2";
            }
        }
        return versionAnterior + "_v2";
    }

    private String calcularJornadaAutomatica(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return "Tarde";
        }
        long horasDuracion = Duration.between(fechaInicio, fechaFin).toHours();
        if (horasDuracion == 24) {
            return "24 Horas";
        }
        int horaInicio = fechaInicio.getHour();
        if (horaInicio >= 6 && horaInicio < 12) {
            return "Mañana";
        } else if (horaInicio >= 12 && horaInicio < 18) {
            return "Tarde";
        } else {
            return "Noche";
        }
    }
}
