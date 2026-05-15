package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.TipoJornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoJornadaRepository extends JpaRepository<TipoJornada, String> {
    List<TipoJornada> findByEstadoTrueOrderByOrdenAsc();
    List<TipoJornada> findByEsTrabajoTrueAndEstadoTrueOrderByOrdenAsc();
    List<TipoJornada> findByEsDescansoTrueAndEstadoTrueOrderByOrdenAsc();
}
