package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.Entidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntidadRepository extends JpaRepository<Entidad, Long> {

    List<Entidad> findByEstadoTrueOrderByNombreAsc();
}
