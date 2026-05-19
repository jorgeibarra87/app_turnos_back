package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.TipoPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoPersonalRepository extends JpaRepository<TipoPersonal, Long> {

    List<TipoPersonal> findByEstadoTrueOrderByNombreAsc();
}
