package com.turnos.enfermeria.repository;

import com.turnos.enfermeria.model.dto.response.PersonaContratoTotalDTO;
import com.turnos.enfermeria.model.entity.Contrato;
import com.turnos.enfermeria.model.entity.PersonaContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaContratoRepository extends JpaRepository<PersonaContrato, Long> {

    @Query("SELECT g.contrato FROM PersonaContrato g WHERE g.persona.idPersona = :personaId")
    Optional<Contrato> findContratoByPersonaId(@Param("personaId") Long personaId);

    @Query("""
    SELECT new com.turnos.enfermeria.model.dto.response.PersonaContratoTotalDTO(
                    p.documento,
                    p.nombreCompleto,
                    p.telefono,
                    p.email,
                    COALESCE(t.titulo, 'Sin profesión'),
                    COALESCE(c.numContrato, 'Sin contrato'),
                    COALESCE(r.rol, 'Sin rol')
                )
                FROM Persona p
                LEFT JOIN p.titulosFormacionAcademica t
                LEFT JOIN PersonaContrato pc ON pc.persona.idPersona = p.idPersona
                LEFT JOIN Contrato c ON pc.contrato = c
                LEFT JOIN p.roles r
                WHERE p.documento = :documento
    """)
    List<PersonaContratoTotalDTO> findAllPersonaInfoByDocumento(@Param("documento") String documento);

    boolean existsByPersona_IdPersonaAndContrato_IdContrato(Long idPersona, Long idContrato);

}
