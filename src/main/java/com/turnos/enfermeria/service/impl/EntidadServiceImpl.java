package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.model.dto.response.EntidadDTO;
import com.turnos.enfermeria.model.entity.Entidad;
import com.turnos.enfermeria.repository.EntidadRepository;
import com.turnos.enfermeria.service.EntidadService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EntidadServiceImpl implements EntidadService {

    private final EntidadRepository entidadRepository;
    private final ModelMapper modelMapper;

    @Override
    public EntidadDTO create(EntidadDTO entidadDTO) {
        Entidad entidad = modelMapper.map(entidadDTO, Entidad.class);
        entidad.setEstado(entidadDTO.getEstado() != null ? entidadDTO.getEstado() : true);
        Entidad guardada = entidadRepository.save(entidad);
        return modelMapper.map(guardada, EntidadDTO.class);
    }

    @Override
    public EntidadDTO update(EntidadDTO entidadDTO, Long id) {
        Entidad existente = entidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entidad no encontrada: " + id));

        if (entidadDTO.getNombre() != null) existente.setNombre(entidadDTO.getNombre());
        if (entidadDTO.getSigla() != null) existente.setSigla(entidadDTO.getSigla());
        if (entidadDTO.getEstado() != null) existente.setEstado(entidadDTO.getEstado());

        Entidad actualizada = entidadRepository.save(existente);
        return modelMapper.map(actualizada, EntidadDTO.class);
    }

    @Override
    public Optional<EntidadDTO> findById(Long id) {
        return entidadRepository.findById(id)
                .map(e -> modelMapper.map(e, EntidadDTO.class));
    }

    @Override
    public List<EntidadDTO> findAll() {
        return entidadRepository.findAll()
                .stream()
                .map(e -> modelMapper.map(e, EntidadDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Entidad entidad = entidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entidad no encontrada: " + id));
        entidadRepository.delete(entidad);
    }
}
