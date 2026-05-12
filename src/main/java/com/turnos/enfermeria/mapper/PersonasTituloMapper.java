package com.turnos.enfermeria.mapper;

import com.turnos.enfermeria.model.dto.response.PersonaTituloDTO;
import com.turnos.enfermeria.model.dto.response.TitulosFormacionAcademicaDTO;
import com.turnos.enfermeria.model.entity.Persona;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonasTituloMapper {

    public PersonaTituloDTO toDTO(Persona persona) {
        PersonaTituloDTO dto = new PersonaTituloDTO();
        dto.setIdPersona(persona.getIdPersona());
        dto.setNombreCompleto(persona.getNombreCompleto());
        dto.setDocumento(persona.getDocumento());

        List<TitulosFormacionAcademicaDTO> titulosDTO = persona.getTitulosFormacionAcademica().stream().map(titulo -> {
            TitulosFormacionAcademicaDTO t = new TitulosFormacionAcademicaDTO();
            t.setIdTitulo(titulo.getIdTitulo());
            t.setTitulo(titulo.getTitulo());
            t.setIdTipoFormacionAcademica(titulo.getIdTipoFormacionAcademica());
            return t;
        }).collect(Collectors.toList());

        dto.setTitulos(titulosDTO);

        return dto;
    }

    public List<PersonaTituloDTO> toDTOList(List<Persona> personas) {
        return personas.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
