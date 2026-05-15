package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.model.dto.response.TipoJornadaDTO;
import com.turnos.enfermeria.model.entity.TipoJornada;
import com.turnos.enfermeria.repository.TipoJornadaRepository;
import com.turnos.enfermeria.service.TipoJornadaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TipoJornadaServiceImpl implements TipoJornadaService {

    private final TipoJornadaRepository tipoJornadaRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TipoJornadaDTO> findAll() {
        return tipoJornadaRepository.findAll().stream()
                .map(tj -> modelMapper.map(tj, TipoJornadaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoJornadaDTO> findActivos() {
        return tipoJornadaRepository.findByEstadoTrueOrderByOrdenAsc().stream()
                .map(tj -> modelMapper.map(tj, TipoJornadaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoJornadaDTO> findTrabajo() {
        return tipoJornadaRepository.findByEsTrabajoTrueAndEstadoTrueOrderByOrdenAsc().stream()
                .map(tj -> modelMapper.map(tj, TipoJornadaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoJornadaDTO> findDescanso() {
        return tipoJornadaRepository.findByEsDescansoTrueAndEstadoTrueOrderByOrdenAsc().stream()
                .map(tj -> modelMapper.map(tj, TipoJornadaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoJornadaDTO> findById(String codigo) {
        return tipoJornadaRepository.findById(codigo)
                .map(tj -> modelMapper.map(tj, TipoJornadaDTO.class));
    }

    @Override
    public TipoJornadaDTO save(TipoJornadaDTO dto) {
        TipoJornada entity = modelMapper.map(dto, TipoJornada.class);
        entity = tipoJornadaRepository.save(entity);
        return modelMapper.map(entity, TipoJornadaDTO.class);
    }

    @Override
    public TipoJornadaDTO update(String codigo, TipoJornadaDTO dto) {
        TipoJornada existente = tipoJornadaRepository.findById(codigo)
                .orElseThrow(() -> new EntityNotFoundException("TipoJornada no encontrado: " + codigo));
        existente.setNombre(dto.getNombre());
        existente.setHoraInicio(dto.getHoraInicio());
        existente.setHoraFin(dto.getHoraFin());
        existente.setEsDescanso(dto.getEsDescanso());
        existente.setEsTrabajo(dto.getEsTrabajo());
        existente.setColor(dto.getColor());
        existente.setOrden(dto.getOrden());
        existente.setEstado(dto.getEstado());
        existente = tipoJornadaRepository.save(existente);
        return modelMapper.map(existente, TipoJornadaDTO.class);
    }

    @Override
    public void delete(String codigo) {
        tipoJornadaRepository.deleteById(codigo);
    }
}
