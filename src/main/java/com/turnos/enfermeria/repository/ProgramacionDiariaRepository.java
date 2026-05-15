package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.entity.ProgramacionDiaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramacionDiariaRepository extends JpaRepository<ProgramacionDiaria, Long> {

    List<ProgramacionDiaria> findByCuadroTurno_IdCuadroTurnoOrderByPersona_NombreCompletoAscDiaMesAsc(Long idCuadroTurno);

    Optional<ProgramacionDiaria> findByCuadroTurno_IdCuadroTurnoAndPersona_IdPersonaAndDiaMes(
            Long idCuadroTurno, Long idPersona, Integer diaMes);

    boolean existsByCuadroTurno_IdCuadroTurnoAndPersona_IdPersonaAndDiaMes(
            Long idCuadroTurno, Long idPersona, Integer diaMes);

    long countByCuadroTurno_IdCuadroTurno(Long idCuadroTurno);

    @Modifying
    @Query("DELETE FROM ProgramacionDiaria pd WHERE pd.cuadroTurno.idCuadroTurno = :idCuadroTurno")
    void deleteByCuadroTurnoId(@Param("idCuadroTurno") Long idCuadroTurno);

    List<ProgramacionDiaria> findByCuadroTurno_IdCuadroTurnoAndPersona_IdPersona(
            Long idCuadroTurno, Long idPersona);
}
