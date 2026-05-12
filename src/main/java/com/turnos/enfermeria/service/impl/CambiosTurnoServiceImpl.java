package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.model.dto.response.CambiosTurnoDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.CambiosTurnoRepository;
import com.turnos.enfermeria.repository.CuadroTurnoRepository;
import com.turnos.enfermeria.repository.PersonaRepository;
import com.turnos.enfermeria.repository.TurnosRepository;
import com.turnos.enfermeria.service.CambiosTurnoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CambiosTurnoServiceImpl implements CambiosTurnoService {

    private final CambiosTurnoRepository cambiosTurnoRepo;
    private final ModelMapper modelMapper;
    private final TurnosRepository turnosRepository;
    private final CuadroTurnoRepository cuadroTurnoRepository;
    private final PersonaRepository personaRepository;

    public CambiosTurnoDTO create(CambiosTurnoDTO cambiosTurnoDTO) {
        Turnos turno = buscarTurno(cambiosTurnoDTO.getIdTurno());
        CuadroTurno cuadroTurno = buscarCuadroTurno(cambiosTurnoDTO.getIdCuadroTurno());
        Persona persona = buscarPersona(cambiosTurnoDTO.getIdUsuario());

        CambiosTurno cambiosTurno = modelMapper.map(cambiosTurnoDTO, CambiosTurno.class);
        configurarCambioTurno(cambiosTurno, cambiosTurnoDTO, turno, cuadroTurno, persona);

        CambiosTurno cambioGuardado = cambiosTurnoRepo.save(cambiosTurno);
        return modelMapper.map(cambioGuardado, CambiosTurnoDTO.class);
    }

    public CambiosTurnoDTO update(CambiosTurnoDTO detalleCambiosTurnoDTO, Long id) {
        CambiosTurno cambioExistente = buscarCambioTurno(id);

        Turnos turno = buscarTurno(detalleCambiosTurnoDTO.getIdTurno());
        CuadroTurno cuadroTurno = buscarCuadroTurno(detalleCambiosTurnoDTO.getIdCuadroTurno());
        Persona persona = buscarPersona(detalleCambiosTurnoDTO.getIdUsuario());

        actualizarCambioExistente(cambioExistente, detalleCambiosTurnoDTO, turno, cuadroTurno, persona);

        CambiosTurno cambioActualizado = cambiosTurnoRepo.save(cambioExistente);
        return modelMapper.map(cambioActualizado, CambiosTurnoDTO.class);
    }

    public Optional<CambiosTurnoDTO> findById(Long idCambio) {
        return cambiosTurnoRepo.findById(idCambio)
                .map(cambiosTurno -> modelMapper.map(cambiosTurno, CambiosTurnoDTO.class));
    }

    public List<CambiosTurnoDTO> findAll() {
        return cambiosTurnoRepo.findAll()
                .stream()
                .map(cambiosTurno -> modelMapper.map(cambiosTurno, CambiosTurnoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idCambio) {
        Optional<CambiosTurno> optionalCambio = cambiosTurnoRepo.findById(idCambio);
        if (optionalCambio.isPresent()) {
            cambiosTurnoRepo.deleteById(idCambio);
        } else {
            throw new EntityNotFoundException("Cambio de turno no encontrado");
        }
    }

    public List<CambiosTurnoDTO> obtenerCambiosPorTurno(Long idTurno) {
        List<CambiosTurno> cambios = cambiosTurnoRepo.findByTurno_IdTurno(idTurno);
        return cambios.stream()
                .map(this::mapearCambioConRelaciones)
                .collect(Collectors.toList());
    }

    private Turnos buscarTurno(Long id) {
        return id != null ? turnosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado")) : null;
    }

    private CuadroTurno buscarCuadroTurno(Long id) {
        return id != null ? cuadroTurnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CuadroTurno no encontrado")) : null;
    }

    private Persona buscarPersona(Long id) {
        return id != null ? personaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada")) : null;
    }

    private CambiosTurno buscarCambioTurno(Long id) {
        return cambiosTurnoRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CambiosTurno no encontrado"));
    }

    private void configurarCambioTurno(CambiosTurno cambio, CambiosTurnoDTO dto,
                                       Turnos turno, CuadroTurno cuadroTurno, Persona persona) {
        cambio.setTurno(turno);
        cambio.setCuadroTurno(cuadroTurno);
        cambio.setUsuario(persona);
        cambio.setFechaCambio(dto.getFechaCambio());
        cambio.setFechaInicio(dto.getFechaInicio());
        cambio.setFechaFin(dto.getFechaFin());
        cambio.setTotalHoras(dto.getTotalHoras());
        cambio.setTipoTurno(dto.getTipoTurno());
        cambio.setEstadoTurno(dto.getEstadoTurno());
        cambio.setJornada(dto.getJornada());
        cambio.setVersion(dto.getVersion());
        cambio.setComentarios(dto.getComentarios());
    }

    private void actualizarCambioExistente(CambiosTurno cambioExistente, CambiosTurnoDTO dto,
                                           Turnos turno, CuadroTurno cuadroTurno, Persona persona) {
        if (dto.getIdTurno() != null) cambioExistente.setTurno(turno);
        if (dto.getIdCuadroTurno() != null) cambioExistente.setCuadroTurno(cuadroTurno);
        if (dto.getIdUsuario() != null) cambioExistente.setUsuario(persona);
        if (dto.getIdCambio() != null) cambioExistente.setIdCambio(dto.getIdCambio());
        if (dto.getFechaCambio() != null) cambioExistente.setFechaCambio(dto.getFechaCambio());
        if (dto.getFechaInicio() != null) cambioExistente.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) cambioExistente.setFechaFin(dto.getFechaFin());
        if (dto.getTotalHoras() != null) cambioExistente.setTotalHoras(dto.getTotalHoras());
        if (dto.getTipoTurno() != null) cambioExistente.setTipoTurno(dto.getTipoTurno());
        if (dto.getEstadoTurno() != null) cambioExistente.setEstadoTurno(dto.getEstadoTurno());
        if (dto.getJornada() != null) cambioExistente.setJornada(dto.getJornada());
        if (dto.getVersion() != null) cambioExistente.setVersion(dto.getVersion());
        if (dto.getComentarios() != null) cambioExistente.setComentarios(dto.getComentarios());
    }

    private CambiosTurnoDTO mapearCambioConRelaciones(CambiosTurno cambio) {
        CambiosTurnoDTO dto = modelMapper.map(cambio, CambiosTurnoDTO.class);
        if (cambio.getTurno() != null) {
            dto.setIdTurno(cambio.getTurno().getIdTurno());
        }
        if (cambio.getUsuario() != null) {
            dto.setIdUsuario(cambio.getUsuario().getIdPersona());
        }
        if (cambio.getCuadroTurno() != null) {
            dto.setIdCuadroTurno(cambio.getCuadroTurno().getIdCuadroTurno());
        }
        return dto;
    }
}
