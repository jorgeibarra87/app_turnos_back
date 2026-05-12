package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.model.dto.response.PersonaContratoDTO;
import com.turnos.enfermeria.model.dto.response.PersonaContratoTotalDTO;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import com.turnos.enfermeria.service.PersonaContratoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PersonaContratoServiceImpl implements PersonaContratoService {

    private final PersonaContratoRepository personaContratoRepository;
    private final ModelMapper modelMapper;
    private final PersonaRepository personaRepository;
    private final TitulosFormacionAcademicaRepository titulosFormacionAcademicaRepository;
    private final ContratoRepository contratoRepository;
    private final RolesRepository rolesRepository;

    public PersonaContratoDTO create(PersonaContratoDTO personaContratoDTO) {
        Persona persona = personaRepository.findById(personaContratoDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("La persona es obligatoria."));

        Contrato contrato = contratoRepository.findById(personaContratoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("El contrato es obligatorio."));

        Roles roles = rolesRepository.findById(personaContratoDTO.getIdRol())
                .orElseThrow(() -> new RuntimeException("rol no encontrado"));

        PersonaContrato personaContrato = modelMapper.map(personaContratoDTO, PersonaContrato.class);
        personaContrato.setIdPersonaContrato(personaContratoDTO.getIdPersonaContrato());
        personaContrato.setPersona(persona);
        personaContrato.setContrato(contrato);
        personaContrato.setRoles(roles);

        PersonaContrato personaContratoGuardado = personaContratoRepository.save(personaContrato);
        return modelMapper.map(personaContratoGuardado, PersonaContratoDTO.class);
    }

    public PersonaContratoDTO update(PersonaContratoDTO detallePersonaContratoDTO, Long id) {
        PersonaContrato personaContratoExistente = personaContratoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("persona contrato no encontrado"));

        Persona persona = personaRepository.findById(detallePersonaContratoDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("La persona es obligatoria."));

        Contrato contrato = contratoRepository.findById(detallePersonaContratoDTO.getIdContrato())
                .orElseThrow(() -> new RuntimeException("El contrato es obligatorio."));

        if (detallePersonaContratoDTO.getIdPersonaContrato() != null) {
            personaContratoExistente.setIdPersonaContrato(detallePersonaContratoDTO.getIdPersonaContrato());
        }
        if (detallePersonaContratoDTO.getIdPersona() != null) {
            personaContratoExistente.setPersona(persona);
        }
        if (detallePersonaContratoDTO.getIdContrato() != null) {
            personaContratoExistente.setContrato(contrato);
        }

        PersonaContrato personaContratoActualizado = personaContratoRepository.save(personaContratoExistente);
        return modelMapper.map(personaContratoActualizado, PersonaContratoDTO.class);
    }

    public Optional<PersonaContratoDTO> findById(Long idPersonaContrato) {
        return personaContratoRepository.findById(idPersonaContrato)
                .map(personaContrato -> modelMapper.map(personaContrato, PersonaContratoDTO.class));
    }

    public List<PersonaContratoDTO> findAll() {
        return personaContratoRepository.findAll()
                .stream()
                .map(personaContrato -> modelMapper.map(personaContrato, PersonaContratoDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idPersonaContrato) {
        PersonaContrato personaContratoEliminar = personaContratoRepository.findById(idPersonaContrato)
                .orElseThrow(() -> new EntityNotFoundException("persona contrato no encontrado"));
        personaContratoRepository.deleteById(idPersonaContrato);
    }

    public PersonaContratoTotalDTO obtenerInformacionPersonaCompleta(String documento) {
        List<PersonaContratoTotalDTO> resultados = personaContratoRepository.findAllPersonaInfoByDocumento(documento);

        if (resultados.isEmpty()) {
            throw new EntityNotFoundException("Persona no encontrada con documento: " + documento);
        }

        PersonaContratoTotalDTO base = resultados.get(0);

        String profesiones = resultados.stream()
                .map(PersonaContratoTotalDTO::getProfesion)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        String contratos = resultados.stream()
                .map(PersonaContratoTotalDTO::getContrato)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        String roles = resultados.stream()
                .map(PersonaContratoTotalDTO::getRol)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        return new PersonaContratoTotalDTO(
                base.getDocumento(),
                base.getNombre(),
                base.getTelefono(),
                base.getEmail(),
                profesiones.isEmpty() ? "Sin profesión" : profesiones,
                contratos.isEmpty() ? "Sin contrato" : contratos,
                roles.isEmpty() ? "Sin rol" : roles
        );
    }

    @Transactional
    public PersonaContratoTotalDTO savePersonaContratoCompleto(PersonaContratoTotalDTO personaContratoTotalDTO) {
        try {
            Persona persona = personaRepository.findByDocumento(personaContratoTotalDTO.getDocumento())
                    .orElseGet(() -> {
                        Persona nuevaPersona = new Persona();
                        nuevaPersona.setDocumento(personaContratoTotalDTO.getDocumento());
                        nuevaPersona.setNombreCompleto(personaContratoTotalDTO.getNombre());
                        nuevaPersona.setTelefono(personaContratoTotalDTO.getTelefono());
                        nuevaPersona.setEmail(personaContratoTotalDTO.getEmail());
                        nuevaPersona.setEstado(true);
                        return personaRepository.save(nuevaPersona);
                    });

            if (personaContratoTotalDTO.getProfesion() != null &&
                    !personaContratoTotalDTO.getProfesion().equals("Sin profesión")) {

                String[] profesiones = personaContratoTotalDTO.getProfesion().split(", ");
                for (String profesion : profesiones) {
                    if (!profesion.trim().isEmpty()) {
                        TitulosFormacionAcademica titulo = new TitulosFormacionAcademica();
                        titulo.setTitulo(profesion.trim());
                        titulo.setEstado(true);
                        titulosFormacionAcademicaRepository.save(titulo);
                    }
                }
            }

            if (personaContratoTotalDTO.getContrato() != null &&
                    !personaContratoTotalDTO.getContrato().equals("Sin contrato")) {

                String[] contratos = personaContratoTotalDTO.getContrato().split(", ");
                String[] rolesArray = personaContratoTotalDTO.getRol() != null &&
                        !personaContratoTotalDTO.getRol().equals("Sin rol") ?
                        personaContratoTotalDTO.getRol().split(", ") : new String[]{};

                for (int i = 0; i < contratos.length; i++) {
                    String numeroContrato = contratos[i].trim();
                    if (!numeroContrato.isEmpty()) {
                        Contrato contrato = contratoRepository.findByNumContrato(numeroContrato)
                                .orElseGet(() -> {
                                    Contrato nuevoContrato = new Contrato();
                                    nuevoContrato.setNumContrato(numeroContrato);
                                    nuevoContrato.setEstado(true);
                                    return contratoRepository.save(nuevoContrato);
                                });

                        Roles rol = null;
                        if (i < rolesArray.length && !rolesArray[i].trim().isEmpty()) {
                            int finalI = i;
                            rol = rolesRepository.findByRol(rolesArray[i].trim())
                                    .orElseGet(() -> {
                                        Roles nuevoRol = new Roles();
                                        nuevoRol.setRol(rolesArray[finalI].trim());
                                        nuevoRol.setEstado(true);
                                        return rolesRepository.save(nuevoRol);
                                    });
                        }

                        boolean existePersonaContrato = personaContratoRepository
                                .existsByPersona_IdPersonaAndContrato_IdContrato(
                                        persona.getIdPersona(),
                                        contrato.getIdContrato()
                                );

                        if (!existePersonaContrato) {
                            PersonaContrato personaContrato = new PersonaContrato();
                            personaContrato.setPersona(persona);
                            personaContrato.setContrato(contrato);
                            personaContrato.setRoles(rol);
                            personaContrato.setEstado(true);
                            personaContratoRepository.save(personaContrato);
                        }
                    }
                }
            }

            return obtenerInformacionPersonaCompleta(personaContratoTotalDTO.getDocumento());

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la persona contrato completo: " + e.getMessage(), e);
        }
    }

    @Transactional
    public PersonaContratoTotalDTO updatePersonaContratoCompleto(String documento,
                                                                  PersonaContratoTotalDTO personaContratoTotalDTO) {
        try {
            Persona persona = personaRepository.findByDocumento(documento)
                    .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada con documento: " + documento));

            persona.setNombreCompleto(personaContratoTotalDTO.getNombre());
            persona.setTelefono(personaContratoTotalDTO.getTelefono());
            persona.setEmail(personaContratoTotalDTO.getEmail());
            personaRepository.save(persona);

            if (personaContratoTotalDTO.getProfesion() != null &&
                    !personaContratoTotalDTO.getProfesion().equals("Sin profesión")) {

                String[] profesiones = personaContratoTotalDTO.getProfesion().split(", ");
                for (String profesion : profesiones) {
                    if (!profesion.trim().isEmpty()) {
                        TitulosFormacionAcademica titulo = new TitulosFormacionAcademica();
                        titulo.setTitulo(profesion.trim());
                        titulo.setEstado(true);
                        titulosFormacionAcademicaRepository.save(titulo);
                    }
                }
            }

            return obtenerInformacionPersonaCompleta(documento);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la persona contrato completo: " + e.getMessage(), e);
        }
    }
}
