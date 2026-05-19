package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.model.dto.response.TipoPersonalDTO;
import com.turnos.enfermeria.model.entity.TipoPersonal;
import com.turnos.enfermeria.repository.TipoPersonalRepository;
import com.turnos.enfermeria.service.TipoPersonalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TipoPersonalServiceImpl implements TipoPersonalService {

    private final TipoPersonalRepository tipoPersonalRepository;
    private final ModelMapper modelMapper;

    @Override
    public TipoPersonalDTO create(TipoPersonalDTO tipoPersonalDTO) {
        TipoPersonal tp = modelMapper.map(tipoPersonalDTO, TipoPersonal.class);
        tp.setEstado(tipoPersonalDTO.getEstado() != null ? tipoPersonalDTO.getEstado() : true);
        TipoPersonal guardado = tipoPersonalRepository.save(tp);
        return modelMapper.map(guardado, TipoPersonalDTO.class);
    }

    @Override
    public TipoPersonalDTO update(TipoPersonalDTO tipoPersonalDTO, Long id) {
        TipoPersonal existente = tipoPersonalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de personal no encontrado: " + id));

        if (tipoPersonalDTO.getNombre() != null) existente.setNombre(tipoPersonalDTO.getNombre());
        if (tipoPersonalDTO.getSigla() != null) existente.setSigla(tipoPersonalDTO.getSigla());
        if (tipoPersonalDTO.getEstado() != null) existente.setEstado(tipoPersonalDTO.getEstado());

        TipoPersonal actualizado = tipoPersonalRepository.save(existente);
        return modelMapper.map(actualizado, TipoPersonalDTO.class);
    }

    @Override
    public Optional<TipoPersonalDTO> findById(Long id) {
        return tipoPersonalRepository.findById(id)
                .map(tp -> modelMapper.map(tp, TipoPersonalDTO.class));
    }

    @Override
    public List<TipoPersonalDTO> findAll() {
        return tipoPersonalRepository.findAll()
                .stream()
                .map(tp -> modelMapper.map(tp, TipoPersonalDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        TipoPersonal tp = tipoPersonalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de personal no encontrado: " + id));
        tipoPersonalRepository.delete(tp);
    }
}
