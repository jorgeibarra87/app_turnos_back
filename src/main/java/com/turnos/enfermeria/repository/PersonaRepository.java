package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long>, JpaSpecificationExecutor<Persona> {

    Optional<Persona> findByDocumento(String documento);

    List<Persona> findDistinctByEquipos_IdEquipo(Long idEquipo);

    List<Persona> findPersonasByEquipos_IdEquipo(Long idEquipo);

    List<Persona> findPersonasByRoles_IdRol(Long idRol);

    List<Persona> findPersonasByTitulosFormacionAcademica_IdTitulo(Long idTitulo);
}
