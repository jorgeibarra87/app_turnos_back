package com.turnos.enfermeria.service.impl;

import com.turnos.enfermeria.events.CambioPersonaEquipoEvent;
import com.turnos.enfermeria.mapper.*;
import com.turnos.enfermeria.model.dto.response.*;
import com.turnos.enfermeria.model.entity.*;
import com.turnos.enfermeria.repository.*;
import com.turnos.enfermeria.service.CambiosEquipoService;
import com.turnos.enfermeria.service.PersonaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepo;
    private final RolesRepository rolesRepository;
    private final TitulosFormacionAcademicaRepository titulosFormacionAcademicaRepository;
    private final EquipoRepository equipoRepository;
    private final ModelMapper modelMapper;
    private final PersonasEquipoMapper personasEquipoMapper;
    private final PersonasTituloMapper personasTituloMapper;
    private final TituloFormacionAcademicaMapper tituloFormacionAcademicaMapper;
    private final EquipoMapper equipoMapper;
    private final RolMapper rolMapper;
    private final PersonaRolMapper personaRolMapper;
    private final CambiosEquipoService cambiosEquipoService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PersonaDTO create(PersonaDTO personaDTO) {
        Optional<Persona> personaExistente = personaRepo.findByDocumento(personaDTO.getDocumento());
        if (personaExistente.isPresent()) {
            Long idPersonaExistente = personaExistente.get().getIdPersona();
            return update(personaDTO, idPersonaExistente);
        }
        Persona persona = modelMapper.map(personaDTO, Persona.class);
        persona.setIdPersona(null);
        Persona personaGuardada = personaRepo.save(persona);
        return modelMapper.map(personaGuardada, PersonaDTO.class);
    }

    @Transactional
    public PersonaDTO update(PersonaDTO detallePersonaDTO, Long id) {
        Persona personaExistente = personaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        if (detallePersonaDTO.getFechaNacimiento() != null) {
            personaExistente.setFechaNacimiento(detallePersonaDTO.getFechaNacimiento());
        }
        if (detallePersonaDTO.getApellidos() != null && !detallePersonaDTO.getApellidos().trim().isEmpty()) {
            personaExistente.setApellidos(detallePersonaDTO.getApellidos());
        }
        if (detallePersonaDTO.getDocumento() != null && !detallePersonaDTO.getDocumento().trim().isEmpty()) {
            personaExistente.setDocumento(detallePersonaDTO.getDocumento());
        }
        if (detallePersonaDTO.getEmail() != null && !detallePersonaDTO.getEmail().trim().isEmpty()) {
            personaExistente.setEmail(detallePersonaDTO.getEmail());
        }
        if (detallePersonaDTO.getNombreCompleto() != null && !detallePersonaDTO.getNombreCompleto().trim().isEmpty()) {
            personaExistente.setNombreCompleto(detallePersonaDTO.getNombreCompleto());
        }
        if (detallePersonaDTO.getNombres() != null && !detallePersonaDTO.getNombres().trim().isEmpty()) {
            personaExistente.setNombres(detallePersonaDTO.getNombres());
        }
        Persona personaActualizada = personaRepo.save(personaExistente);
        return modelMapper.map(personaActualizada, PersonaDTO.class);
    }

    public Optional<PersonaDTO> findById(Long idPersona) {
        return personaRepo.findById(idPersona)
                .map(persona -> modelMapper.map(persona, PersonaDTO.class));
    }

    public List<PersonaDTO> findAll() {
        return personaRepo.findAll()
                .stream()
                .map(persona -> modelMapper.map(persona, PersonaDTO.class))
                .collect(Collectors.toList());
    }

    public void delete(Long idPersona) {
        Persona personaEliminar = personaRepo.findById(idPersona)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));
        if (personaEliminar.getEquipos() != null) {
            for (Equipo equipo : personaEliminar.getEquipos()) {
                cambiosEquipoService.registrarCambioPersonaEquipo(
                        personaEliminar,
                        equipo,
                        null,
                        "DESVINCULACION"
                );
            }
        }
        personaRepo.deleteById(idPersona);
    }

    // ========== EQUIPO RELATION METHODS ==========

    @Transactional
    public PersonaEquipoDTO agregarEquipoAUsuario(Long idPersona, Long idEquipo) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        if (!persona.getEquipos().contains(equipo)) {
            cambiosEquipoService.registrarCambioPersonaEquipo(
                    persona, null, equipo, "ASIGNACION"
            );
        }
        persona.getEquipos().add(equipo);
        Persona personaActualizada = personaRepo.save(persona);
        return personasEquipoMapper.toDTO(personaActualizada);
    }

    @Transactional
    public EquipoDTO agregarUsuarioAEquipo(Long idEquipo, Long idPersona) {
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        persona.getEquipos().add(equipo);
        personaRepo.save(persona);
        return equipoMapper.toDTO(equipo);
    }

    public List<EquipoDTO> obtenerEquiposDeUsuario(Long idPersona) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        return equipoMapper.toDTOList(new ArrayList<>(persona.getEquipos()));
    }

    public List<PersonaEquipoDTO> obtenerUsuariosPorEquipo(Long idEquipo) {
        List<Persona> personas = personaRepo.findDistinctByEquipos_IdEquipo(idEquipo);
        List<PersonaEquipoDTO> resultado = new ArrayList<>();
        for (Persona persona : personas) {
            PersonaEquipoDTO dto = new PersonaEquipoDTO();
            dto.setIdPersona(persona.getIdPersona());
            dto.setNombreCompleto(persona.getNombreCompleto());
            dto.setDocumento(persona.getDocumento());
            List<EquipoDTO> equipos = persona.getEquipos().stream()
                    .map(equipo -> new EquipoDTO(equipo.getIdEquipo(), equipo.getNombre(), equipo.getEstado(), equipo.getObservaciones()))
                    .distinct()
                    .collect(Collectors.toList());
            dto.setEquipos(equipos);
            resultado.add(dto);
        }
        return resultado;
    }

    @Transactional
    public EquipoDTO actualizarUsuariosDeEquipo(Long idEquipo, List<Long> nuevosUsuariosIds) {
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        List<Persona> personasActualesDelEquipo = personaRepo.findDistinctByEquipos_IdEquipo(idEquipo);
        for (Persona persona : personasActualesDelEquipo) {
            boolean sigueAsignado = nuevosUsuariosIds.contains(persona.getIdPersona());
            if (!sigueAsignado) {
                cambiosEquipoService.registrarCambioPersonaEquipo(
                        persona, equipo, null, "DESVINCULACION"
                );
                eventPublisher.publishEvent(new CambioPersonaEquipoEvent(
                        persona.getIdPersona(), idEquipo,
                        "DESVINCULACIÓN DE PERSONA",
                        "Se ha desvinculado a " + persona.getNombreCompleto() +
                                " del equipo " + equipo.getNombre()
                ));
            }
            persona.getEquipos().remove(equipo);
        }
        personaRepo.saveAll(personasActualesDelEquipo);
        List<Persona> nuevasPersonas = personaRepo.findAllById(nuevosUsuariosIds);
        for (Persona persona : nuevasPersonas) {
            boolean yaEstabaAsignado = personasActualesDelEquipo.stream()
                    .anyMatch(p -> p.getIdPersona().equals(persona.getIdPersona()));
            if (!yaEstabaAsignado) {
                cambiosEquipoService.registrarCambioPersonaEquipo(
                        persona, null, equipo, "ASIGNACION"
                );
                eventPublisher.publishEvent(new CambioPersonaEquipoEvent(
                        persona.getIdPersona(), idEquipo,
                        "ASIGNACIÓN DE PERSONA",
                        "Se ha asignado a " + persona.getNombreCompleto() +
                                " al equipo " + equipo.getNombre()
                ));
            }
            if (!persona.getEquipos().contains(equipo)) {
                persona.getEquipos().add(equipo);
            }
        }
        personaRepo.saveAll(nuevasPersonas);
        return equipoMapper.toDTO(equipo);
    }

    public PersonaEquipoDTO actualizarEquiposDeUsuario(Long idPersona, List<Long> nuevosEquiposIds) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        List<Equipo> equiposAnteriores = new ArrayList<>(persona.getEquipos());
        persona.getEquipos().clear();
        List<Equipo> nuevosEquipos = equipoRepository.findAllById(nuevosEquiposIds);
        for (Equipo equipoAnterior : equiposAnteriores) {
            boolean sigueAsignado = nuevosEquipos.stream()
                    .anyMatch(eq -> eq.getIdEquipo().equals(equipoAnterior.getIdEquipo()));
            if (!sigueAsignado) {
                cambiosEquipoService.registrarCambioPersonaEquipo(
                        persona, equipoAnterior, null, "DESVINCULACION"
                );
            }
        }
        for (Equipo equipoNuevo : nuevosEquipos) {
            boolean yaEstabaAsignado = equiposAnteriores.stream()
                    .anyMatch(eq -> eq.getIdEquipo().equals(equipoNuevo.getIdEquipo()));
            if (!yaEstabaAsignado) {
                cambiosEquipoService.registrarCambioPersonaEquipo(
                        persona, null, equipoNuevo, "ASIGNACION"
                );
            }
        }
        persona.getEquipos().addAll(nuevosEquipos);
        Persona personaActualizada = personaRepo.save(persona);
        return personasEquipoMapper.toDTO(personaActualizada);
    }

    @Transactional
    public void eliminarEquipoDeUsuario(Long idPersona, Long idEquipo) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        if (persona.getEquipos().contains(equipo)) {
            cambiosEquipoService.registrarCambioPersonaEquipo(
                    persona, equipo, null, "DESVINCULACION"
            );
        }
        persona.getEquipos().remove(equipo);
        personaRepo.save(persona);
    }

    // ========== TITULO RELATION METHODS ==========

    @Transactional
    public Persona asignarTituloAUsuario(Long idPersona, Long idTitulo) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró PERSONA con ID: " + idPersona));
        TitulosFormacionAcademica titulo = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró TITULO con ID: " + idTitulo));
        if (persona.getTitulosFormacionAcademica() == null) {
            persona.setTitulosFormacionAcademica(new HashSet<>());
        }
        if (!persona.getTitulosFormacionAcademica().contains(titulo)) {
            persona.getTitulosFormacionAcademica().add(titulo);
        }
        return personaRepo.save(persona);
    }

    @Transactional
    public TitulosFormacionAcademicaDTO agregarUsuarioATitulo(Long idTitulo, Long idPersona) {
        TitulosFormacionAcademica titulo = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("Titulo no encontrado"));
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        persona.getTitulosFormacionAcademica().add(titulo);
        personaRepo.save(persona);
        return tituloFormacionAcademicaMapper.toDTO(titulo);
    }

    public List<TitulosFormacionAcademicaDTO> obtenerTituloDeUsuario(Long idPersona) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        return tituloFormacionAcademicaMapper.toDTOList(new ArrayList<>(persona.getTitulosFormacionAcademica()));
    }

    public List<PersonaTituloDTO> obtenerUsuariosPorTitulo(Long idTitulo) {
        List<Persona> personas = personaRepo.findPersonasByTitulosFormacionAcademica_IdTitulo(idTitulo);
        return personas.stream()
                .map(personasTituloMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TitulosFormacionAcademicaDTO actualizarUsuariosDeTitulo(Long idTitulo, List<Long> nuevosUsuariosIds) {
        TitulosFormacionAcademica titulo = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("titulo no encontrado"));
        List<Persona> todas = personaRepo.findAll();
        for (Persona persona : todas) {
            persona.getTitulosFormacionAcademica().remove(titulo);
        }
        personaRepo.saveAll(todas);
        List<Persona> nuevasPersonas = personaRepo.findAllById(nuevosUsuariosIds);
        for (Persona persona : nuevasPersonas) {
            persona.getTitulosFormacionAcademica().add(titulo);
        }
        personaRepo.saveAll(nuevasPersonas);
        return tituloFormacionAcademicaMapper.toDTO(titulo);
    }

    @Transactional
    public PersonaTituloDTO actualizarTitulosDeUsuario(Long idPersona, List<Long> nuevosTitulosIds) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        persona.getTitulosFormacionAcademica().clear();
        List<TitulosFormacionAcademica> nuevosTitulos = titulosFormacionAcademicaRepository.findAllById(nuevosTitulosIds);
        persona.getTitulosFormacionAcademica().addAll(nuevosTitulos);
        Persona personaActualizada = personaRepo.save(persona);
        return personasTituloMapper.toDTO(personaActualizada);
    }

    @Transactional
    public void eliminarTituloDeUsuario(Long idPersona, Long idTitulo) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        TitulosFormacionAcademica titulo = titulosFormacionAcademicaRepository.findById(idTitulo)
                .orElseThrow(() -> new RuntimeException("Titulo no encontrado"));
        persona.getTitulosFormacionAcademica().remove(titulo);
        personaRepo.save(persona);
    }

    // ========== ROL RELATION METHODS ==========

    @Transactional
    public PersonaRolDTO agregarRolAUsuario(Long idPersona, Long idRol) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        Roles roles = rolesRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("rol no encontrado"));
        persona.getRoles().add(roles);
        Persona personaActualizada = personaRepo.save(persona);
        return personaRolMapper.toDTO(personaActualizada);
    }

    @Transactional
    public RolesDTO agregarUsuarioARol(Long idRol, Long idPersona) {
        Roles roles = rolesRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("rol no encontrado"));
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        persona.getRoles().add(roles);
        personaRepo.save(persona);
        return rolMapper.toDTO(roles);
    }

    public List<RolesDTO> obtenerRolDeUsuario(Long idPersona) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        return rolMapper.toDTOList(new ArrayList<>(persona.getRoles()));
    }

    public List<PersonaRolDTO> obtenerUsuariosPorRol(Long idRol) {
        List<Persona> personas = personaRepo.findPersonasByRoles_IdRol(idRol);
        return personas.stream()
                .map(personaRolMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RolesDTO actualizarUsuariosDeRol(Long idRol, List<Long> nuevosUsuariosIds) {
        Roles roles = rolesRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("rol no encontrado"));
        List<Persona> todas = personaRepo.findAll();
        for (Persona persona : todas) {
            persona.getRoles().remove(roles);
        }
        personaRepo.saveAll(todas);
        List<Persona> nuevasPersonas = personaRepo.findAllById(nuevosUsuariosIds);
        for (Persona persona : nuevasPersonas) {
            persona.getRoles().add(roles);
        }
        personaRepo.saveAll(nuevasPersonas);
        return rolMapper.toDTO(roles);
    }

    @Transactional
    public PersonaRolDTO actualizarRolesDeUsuario(Long idPersona, List<Long> nuevosRolesIds) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        persona.getRoles().clear();
        List<Roles> nuevosRoles = rolesRepository.findAllById(nuevosRolesIds);
        persona.getRoles().addAll(nuevosRoles);
        Persona personaActualizada = personaRepo.save(persona);
        return personaRolMapper.toDTO(personaActualizada);
    }

    @Transactional
    public void eliminarRolDeUsuario(Long idPersona, Long idRol) {
        Persona persona = personaRepo.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        Roles roles = rolesRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("rol no encontrado"));
        persona.getRoles().remove(roles);
        personaRepo.save(persona);
    }

    // ========== BULK METHODS ==========

    public List<Persona> findAllPersonas() {
        return personaRepo.findAll();
    }
}
