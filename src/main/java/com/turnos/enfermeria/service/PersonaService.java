package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.response.EquipoDTO;
import com.turnos.enfermeria.model.dto.response.PersonaDTO;
import com.turnos.enfermeria.model.dto.response.PersonaEquipoDTO;
import com.turnos.enfermeria.model.dto.response.PersonaRolDTO;
import com.turnos.enfermeria.model.dto.response.PersonaTituloDTO;
import com.turnos.enfermeria.model.dto.response.RolesDTO;
import com.turnos.enfermeria.model.dto.response.TitulosFormacionAcademicaDTO;
import com.turnos.enfermeria.model.entity.Persona;
import java.util.List;
import java.util.Optional;

public interface PersonaService {
    PersonaDTO create(PersonaDTO personaDTO);
    PersonaDTO update(PersonaDTO detallePersonaDTO, Long id);
    Optional<PersonaDTO> findById(Long idPersona);
    List<PersonaDTO> findAll();
    void delete(Long idPersona);
    PersonaEquipoDTO agregarEquipoAUsuario(Long idPersona, Long idEquipo);
    EquipoDTO agregarUsuarioAEquipo(Long idEquipo, Long idPersona);
    List<EquipoDTO> obtenerEquiposDeUsuario(Long idPersona);
    List<PersonaEquipoDTO> obtenerUsuariosPorEquipo(Long idEquipo);
    EquipoDTO actualizarUsuariosDeEquipo(Long idEquipo, List<Long> nuevosUsuariosIds);
    PersonaEquipoDTO actualizarEquiposDeUsuario(Long idPersona, List<Long> nuevosEquiposIds);
    void eliminarEquipoDeUsuario(Long idPersona, Long idEquipo);
    Persona asignarTituloAUsuario(Long idPersona, Long idTitulo);
    TitulosFormacionAcademicaDTO agregarUsuarioATitulo(Long idTitulo, Long idPersona);
    List<TitulosFormacionAcademicaDTO> obtenerTituloDeUsuario(Long idPersona);
    List<PersonaTituloDTO> obtenerUsuariosPorTitulo(Long idTitulo);
    TitulosFormacionAcademicaDTO actualizarUsuariosDeTitulo(Long idTitulo, List<Long> nuevosUsuariosIds);
    PersonaTituloDTO actualizarTitulosDeUsuario(Long idPersona, List<Long> nuevosTitulosIds);
    void eliminarTituloDeUsuario(Long idPersona, Long idTitulo);
    PersonaRolDTO agregarRolAUsuario(Long idPersona, Long idRol);
    RolesDTO agregarUsuarioARol(Long idRol, Long idPersona);
    List<RolesDTO> obtenerRolDeUsuario(Long idPersona);
    List<PersonaRolDTO> obtenerUsuariosPorRol(Long idRol);
    RolesDTO actualizarUsuariosDeRol(Long idRol, List<Long> nuevosUsuariosIds);
    PersonaRolDTO actualizarRolesDeUsuario(Long idPersona, List<Long> nuevosRolesIds);
    void eliminarRolDeUsuario(Long idPersona, Long idRol);
    List<Persona> findAllPersonas();
}
