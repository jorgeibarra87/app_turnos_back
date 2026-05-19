package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.events.CambioCuadroEvent;
import com.turnos.enfermeria.model.dto.request.ProgramacionDiariaRequest;
import com.turnos.enfermeria.model.dto.response.MatrizMensualDTO;
import com.turnos.enfermeria.model.dto.response.TurnoDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import com.turnos.enfermeria.service.ProgramacionDiariaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ProgramacionDiariaServiceImpl implements ProgramacionDiariaService {

    private final ProgramacionDiariaRepository programacionDiariaRepository;
    private final CuadroTurnoRepository cuadroTurnoRepository;
    private final PersonaRepository personaRepository;
    private final TipoJornadaRepository tipoJornadaRepository;
    private final TurnosRepository turnosRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public MatrizMensualDTO obtenerMatrizPorCuadro(Long idCuadroTurno) {
        CuadroTurno cuadro = cuadroTurnoRepository.findById(idCuadroTurno)
                .orElseThrow(() -> new EntityNotFoundException("CuadroTurno no encontrado: " + idCuadroTurno));

        YearMonth yearMonth = YearMonth.of(Integer.parseInt(cuadro.getAnio()), Integer.parseInt(cuadro.getMes()));
        int diasDelMes = yearMonth.lengthOfMonth();

        List<ProgramacionDiaria> registrosPD = programacionDiariaRepository
                .findByCuadroTurno_IdCuadroTurnoOrderByPersona_NombreCompletoAscDiaMesAsc(idCuadroTurno);

        List<Turnos> turnos = turnosRepository.findByCuadroTurno_IdCuadroTurnoAndEstado(idCuadroTurno, true);

        List<Persona> miembrosEquipo = new ArrayList<>();
        if (cuadro.getEquipos() != null) {
            miembrosEquipo = personaRepository.findPersonasByEquipos_IdEquipo(cuadro.getEquipos().getIdEquipo());
        }

        Map<Long, List<ProgramacionDiaria>> pdPorPersona = registrosPD.stream()
                .collect(Collectors.groupingBy(r -> r.getPersona().getIdPersona()));

        Map<Long, List<Turnos>> turnosPorPersona = turnos.stream()
                .filter(t -> t.getUsuario() != null)
                .collect(Collectors.groupingBy(t -> t.getUsuario().getIdPersona()));

        Set<Long> todasLasPersonasIds = new HashSet<>();
        for (Long id : pdPorPersona.keySet()) todasLasPersonasIds.add(id);
        for (Long id : turnosPorPersona.keySet()) todasLasPersonasIds.add(id);
        for (Persona p : miembrosEquipo) todasLasPersonasIds.add(p.getIdPersona());

        List<Persona> todasLasPersonas = personaRepository.findAllById(todasLasPersonasIds);

        Map<String, TipoJornada> tipoJornadaMap = tipoJornadaRepository.findAll().stream()
                .collect(Collectors.toMap(TipoJornada::getCodigo, tj -> tj, (a, b) -> a));

        Comparator<Persona> sortPorNombre = Comparator.comparing(Persona::getNombreCompleto,
                Comparator.nullsLast(String::compareTo));

        List<MatrizMensualDTO.FilaMatriz> filas = new ArrayList<>();
        for (Persona persona : todasLasPersonas.stream().sorted(sortPorNombre).collect(Collectors.toList())) {
            Map<Integer, ProgramacionDiaria> pdDias = pdPorPersona.getOrDefault(persona.getIdPersona(), List.of())
                    .stream().collect(Collectors.toMap(ProgramacionDiaria::getDiaMes, r -> r, (a, b) -> a));

            Map<Integer, Turnos> turnosDias = turnosPorPersona.getOrDefault(persona.getIdPersona(), List.of())
                    .stream()
                    .filter(t -> t.getFechaInicio() != null)
                    .collect(Collectors.toMap(
                            t -> t.getFechaInicio().getDayOfMonth(),
                            t -> t, (a, b) -> a));

            List<MatrizMensualDTO.CeldaMatrizDTO> celdas = new ArrayList<>();
            for (int dia = 1; dia <= diasDelMes; dia++) {
                ProgramacionDiaria pd = pdDias.get(dia);
                if (pd != null) {
                    celdas.add(new MatrizMensualDTO.CeldaMatrizDTO(
                            dia, pd.getTipoJornada().getCodigo(),
                            pd.getTipoJornada().getNombre(),
                            pd.getTipoJornada().getColor(),
                            pd.getTipoJornada().getEsDescanso(),
                            pd.getObservacion()
                    ));
                } else {
                    Turnos turno = turnosDias.get(dia);
                    if (turno != null && turno.getJornada() != null) {
                        String codigo = mapearJornadaACodigo(turno.getJornada());
                        TipoJornada tj = tipoJornadaMap.get(codigo);
                        celdas.add(new MatrizMensualDTO.CeldaMatrizDTO(
                                dia, codigo,
                                tj != null ? tj.getNombre() : turno.getJornada(),
                                tj != null ? tj.getColor() : null,
                                false, turno.getComentarios()
                        ));
                    } else {
                        celdas.add(new MatrizMensualDTO.CeldaMatrizDTO(
                                dia, null, null, null, null, null
                        ));
                    }
                }
            }

            String perfil = persona.getTitulosFormacionAcademica().stream()
                    .map(TitulosFormacionAcademica::getTitulo)
                    .collect(Collectors.joining(", "));

            filas.add(new MatrizMensualDTO.FilaMatriz(
                    persona.getIdPersona(), persona.getNombreCompleto(),
                    persona.getDocumento(), perfil, celdas
            ));
        }

        return new MatrizMensualDTO(
                cuadro.getIdCuadroTurno(), cuadro.getNombre(),
                cuadro.getAnio(), cuadro.getMes(),
                cuadro.getEntidad(), cuadro.getTipoPersonal(),
                cuadro.getNombreEquipo(),
                diasDelMes, filas
        );
    }

    private String mapearJornadaACodigo(String jornada) {
        if (jornada == null) return null;
        String j = jornada.toLowerCase();
        if (j.contains("manana") || j.contains("mañana")) return "M";
        if (j.contains("tarde")) return "T";
        if (j.contains("noche")) return "N";
        if (j.contains("24")) return "D";
        return null;
    }

    @Override
    public MatrizMensualDTO guardarMatrizCompleta(ProgramacionDiariaRequest request) {
        Long idCuadro = request.getIdCuadroTurno();
        CuadroTurno cuadro = cuadroTurnoRepository.findById(idCuadro)
                .orElseThrow(() -> new EntityNotFoundException("CuadroTurno no encontrado: " + idCuadro));

        programacionDiariaRepository.deleteByCuadroTurnoId(idCuadro);

        Set<String> codigosValidos = tipoJornadaRepository.findAll().stream()
                .map(TipoJornada::getCodigo)
                .collect(Collectors.toSet());

        for (ProgramacionDiariaRequest.CeldaMatriz celda : request.getCeldas()) {
            if (celda.getCodigoJornada() == null || celda.getCodigoJornada().isBlank()) continue;

            Persona persona = personaRepository.findById(celda.getIdPersona())
                    .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada: " + celda.getIdPersona()));

            if (!codigosValidos.contains(celda.getCodigoJornada())) {
                log.warn("Código de jornada inválido: {}, saltando celda", celda.getCodigoJornada());
                continue;
            }

            TipoJornada tipoJornada = tipoJornadaRepository.findById(celda.getCodigoJornada()).get();

            ProgramacionDiaria pd = new ProgramacionDiaria();
            pd.setCuadroTurno(cuadro);
            pd.setPersona(persona);
            pd.setDiaMes(celda.getDiaMes());
            pd.setTipoJornada(tipoJornada);
            pd.setObservacion(celda.getObservacion());
        programacionDiariaRepository.save(pd);
                            }

        generarTurnosDesdeMatriz(idCuadro);

        try {
            CambioCuadroEvent evento = new CambioCuadroEvent(
                    idCuadro,
                    "ACTUALIZACIÓN MATRIZ",
                    "Se actualizó la matriz de turnos del cuadro: " + cuadro.getNombre(),
                    "SISTEMA"
            );
            eventPublisher.publishEvent(evento);
            log.info("Evento de actualización de matriz publicado para cuadro ID: {}", idCuadro);
        } catch (Exception e) {
            log.error("Error al publicar evento de matriz: {}", e.getMessage());
        }

        return obtenerMatrizPorCuadro(idCuadro);
    }

    @Override
    public void actualizarCelda(Long idCuadroTurno, Long idPersona, Integer diaMes, String codigoJornada, String observacion) {
        CuadroTurno cuadro = cuadroTurnoRepository.findById(idCuadroTurno)
                .orElseThrow(() -> new EntityNotFoundException("CuadroTurno no encontrado"));

        programacionDiariaRepository.findByCuadroTurno_IdCuadroTurnoAndPersona_IdPersonaAndDiaMes(idCuadroTurno, idPersona, diaMes)
                .ifPresentOrElse(pd -> {
                    if (codigoJornada == null || codigoJornada.isBlank()) {
                        programacionDiariaRepository.delete(pd);
                    } else {
                        TipoJornada tj = tipoJornadaRepository.findById(codigoJornada)
                                .orElseThrow(() -> new EntityNotFoundException("TipoJornada no encontrado: " + codigoJornada));
                        pd.setTipoJornada(tj);
                        pd.setObservacion(observacion);
                        programacionDiariaRepository.save(pd);
                    }
                }, () -> {
                    if (codigoJornada != null && !codigoJornada.isBlank()) {
                        TipoJornada tj = tipoJornadaRepository.findById(codigoJornada)
                                .orElseThrow(() -> new EntityNotFoundException("TipoJornada no encontrado: " + codigoJornada));
                        Persona persona = personaRepository.findById(idPersona)
                                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));
                        ProgramacionDiaria pd = new ProgramacionDiaria();
                        pd.setCuadroTurno(cuadro);
                        pd.setPersona(persona);
                        pd.setDiaMes(diaMes);
                        pd.setTipoJornada(tj);
                        pd.setObservacion(observacion);
                        programacionDiariaRepository.save(pd);
                    }
                });

        regenerarTurnosParaPersona(idCuadroTurno, idPersona);
    }

    @Override
    public void eliminarMatriz(Long idCuadroTurno) {
        programacionDiariaRepository.deleteByCuadroTurnoId(idCuadroTurno);
        turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno).forEach(t -> {
            t.setEstado(false);
            turnosRepository.save(t);
        });
    }

    @Override
    public List<MatrizMensualDTO.FilaMatriz> generarTurnosDesdeMatriz(Long idCuadroTurno) {
        CuadroTurno cuadro = cuadroTurnoRepository.findById(idCuadroTurno)
                .orElseThrow(() -> new EntityNotFoundException("CuadroTurno no encontrado"));

        List<ProgramacionDiaria> registros = programacionDiariaRepository
                .findByCuadroTurno_IdCuadroTurnoOrderByPersona_NombreCompletoAscDiaMesAsc(idCuadroTurno);

        Map<Long, List<ProgramacionDiaria>> porPersona = registros.stream()
                .filter(r -> Boolean.TRUE.equals(r.getTipoJornada().getEsTrabajo()))
                .collect(Collectors.groupingBy(r -> r.getPersona().getIdPersona()));

        List<Turnos> turnosActuales = turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno);
        Map<String, Turnos> turnosPorClave = new HashMap<>();
        for (Turnos t : turnosActuales) {
            if (t.getUsuario() != null && t.getFechaInicio() != null) {
                String clave = t.getUsuario().getIdPersona() + "_" + t.getFechaInicio().toLocalDate().toString();
                turnosPorClave.put(clave, t);
            }
        }

        List<Turnos> nuevosTurnos = new ArrayList<>();
        Set<String> procesadas = new HashSet<>();

        for (Map.Entry<Long, List<ProgramacionDiaria>> entry : porPersona.entrySet()) {
            Long idPersona = entry.getKey();
            Persona persona = personaRepository.findById(idPersona).orElse(null);
            if (persona == null) continue;

            for (ProgramacionDiaria pd : entry.getValue()) {
                TipoJornada tj = pd.getTipoJornada();
                int anio = Integer.parseInt(cuadro.getAnio());
                int mes = Integer.parseInt(cuadro.getMes());

                LocalTime horaInicio = tj.getHoraInicio() != null
                        ? LocalTime.parse(tj.getHoraInicio())
                        : LocalTime.of(6, 0);
                LocalTime horaFin = tj.getHoraFin() != null
                        ? LocalTime.parse(tj.getHoraFin())
                        : LocalTime.of(14, 0);

                LocalDate fecha = LocalDate.of(anio, mes, pd.getDiaMes());

                LocalDateTime fechaInicio = LocalDateTime.of(fecha, horaInicio);
                LocalDateTime fechaFin = horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)
                        ? LocalDateTime.of(fecha.plusDays(1), horaFin)
                        : LocalDateTime.of(fecha, horaFin);

                long totalHoras = java.time.Duration.between(fechaInicio, fechaFin).toHours();
                if (totalHoras < 0) totalHoras += 24;

                String jornadaNombre = determinarJornada(horaInicio, horaFin, totalHoras);

                String clave = idPersona + "_" + fecha.toString();
                Turnos turno = turnosPorClave.get(clave);

                if (turno != null) {
                    turno.setFechaInicio(fechaInicio);
                    turno.setFechaFin(fechaFin);
                    turno.setTotalHoras(totalHoras);
                    turno.setJornada(jornadaNombre);
                    turno.setEstado(true);
                    nuevosTurnos.add(turno);
                } else {
                    turno = new Turnos();
                    turno.setUsuario(persona);
                    turno.setCuadroTurno(cuadro);
                    turno.setFechaInicio(fechaInicio);
                    turno.setFechaFin(fechaFin);
                    turno.setTotalHoras(totalHoras);
                    turno.setJornada(jornadaNombre);
                    turno.setTipoTurno("Presencial");
                    turno.setEstadoTurno("abierto");
                    turno.setVersion(cuadro.getVersion());
                    turno.setComentarios(pd.getObservacion());
                    turno.setEstado(true);
                    nuevosTurnos.add(turno);
                }
                procesadas.add(clave);
            }
        }

        for (Turnos t : turnosActuales) {
            String clave = t.getUsuario() != null && t.getFechaInicio() != null
                    ? t.getUsuario().getIdPersona() + "_" + t.getFechaInicio().toLocalDate().toString()
                    : null;
            if (clave != null && !procesadas.contains(clave)) {
                t.setEstado(false);
                nuevosTurnos.add(t);
            }
        }

        turnosRepository.saveAll(nuevosTurnos);
        log.info("Generados/actualizados {} turnos desde matriz para cuadro {}", nuevosTurnos.size(), idCuadroTurno);

        return obtenerMatrizPorCuadro(idCuadroTurno).getFilas();
    }

    private void regenerarTurnosParaPersona(Long idCuadroTurno, Long idPersona) {
        List<ProgramacionDiaria> registrosPersona = programacionDiariaRepository
                .findByCuadroTurno_IdCuadroTurnoAndPersona_IdPersona(idCuadroTurno, idPersona);

        CuadroTurno cuadro = cuadroTurnoRepository.findById(idCuadroTurno).orElse(null);
        if (cuadro == null) return;

        Persona persona = personaRepository.findById(idPersona).orElse(null);
        if (persona == null) return;

        List<Turnos> turnosPersona = turnosRepository.findByCuadroTurno_IdCuadroTurno(idCuadroTurno).stream()
                .filter(t -> t.getUsuario() != null && t.getUsuario().getIdPersona().equals(idPersona))
                .collect(Collectors.toList());

        Set<String> nuevosDias = new HashSet<>();
        List<Turnos> aGuardar = new ArrayList<>();

        for (ProgramacionDiaria pd : registrosPersona) {
            if (!Boolean.TRUE.equals(pd.getTipoJornada().getEsTrabajo())) continue;

            int anio = Integer.parseInt(cuadro.getAnio());
            int mes = Integer.parseInt(cuadro.getMes());
            LocalTime hi = pd.getTipoJornada().getHoraInicio() != null
                    ? LocalTime.parse(pd.getTipoJornada().getHoraInicio()) : LocalTime.of(6, 0);
            LocalTime hf = pd.getTipoJornada().getHoraFin() != null
                    ? LocalTime.parse(pd.getTipoJornada().getHoraFin()) : LocalTime.of(14, 0);
            LocalDate fecha = LocalDate.of(anio, mes, pd.getDiaMes());
            LocalDateTime fi = LocalDateTime.of(fecha, hi);
            LocalDateTime ff = hf.isBefore(hi) || hf.equals(hi)
                    ? LocalDateTime.of(fecha.plusDays(1), hf) : LocalDateTime.of(fecha, hf);
            long horas = java.time.Duration.between(fi, ff).toHours();
            if (horas < 0) horas += 24;
            String jornada = determinarJornada(hi, hf, horas);

            String clave = idPersona + "_" + fecha.toString();
            nuevosDias.add(clave);

            Optional<Turnos> existente = turnosPersona.stream()
                    .filter(t -> t.getFechaInicio() != null && t.getFechaInicio().toLocalDate().equals(fecha))
                    .findFirst();

            if (existente.isPresent()) {
                Turnos t = existente.get();
                t.setFechaInicio(fi);
                t.setFechaFin(ff);
                t.setTotalHoras(horas);
                t.setJornada(jornada);
                t.setEstado(true);
                aGuardar.add(t);
            } else {
                Turnos t = new Turnos();
                t.setUsuario(persona);
                t.setCuadroTurno(cuadro);
                t.setFechaInicio(fi);
                t.setFechaFin(ff);
                t.setTotalHoras(horas);
                t.setJornada(jornada);
                t.setTipoTurno("Presencial");
                t.setEstadoTurno("abierto");
                t.setVersion(cuadro.getVersion());
                t.setComentarios(pd.getObservacion());
                t.setEstado(true);
                aGuardar.add(t);
            }
        }

        for (Turnos t : turnosPersona) {
            String clave = t.getUsuario().getIdPersona() + "_" + t.getFechaInicio().toLocalDate().toString();
            if (!nuevosDias.contains(clave)) {
                t.setEstado(false);
                aGuardar.add(t);
            }
        }

        turnosRepository.saveAll(aGuardar);
        log.info("Regenerados {} turnos para persona {} en cuadro {}", aGuardar.size(), idPersona, idCuadroTurno);
    }

    private String determinarJornada(LocalTime horaInicio, LocalTime horaFin, long totalHoras) {
        if (totalHoras == 24) return "24 Horas";
        int h = horaInicio.getHour();
        if (h >= 6 && h < 12) return "Mañana";
        if (h >= 12 && h < 18) return "Tarde";
        return "Noche";
    }
}
