package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "programacion_diaria", schema = "public",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_cuadro_turno", "id_persona", "dia_mes"}))
public class ProgramacionDiaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_programacion", nullable = false)
    private Long idProgramacion;

    @ManyToOne
    @JoinColumn(name = "id_cuadro_turno", referencedColumnName = "id_cuadro_turno", nullable = false)
    @JsonIncludeProperties("idCuadroTurno")
    private CuadroTurno cuadroTurno;

    @ManyToOne
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona", nullable = false)
    @JsonIncludeProperties({"idPersona", "nombreCompleto", "documento"})
    private Persona persona;

    @Column(name = "dia_mes", nullable = false)
    private Integer diaMes;

    @ManyToOne
    @JoinColumn(name = "codigo_jornada", referencedColumnName = "codigo", nullable = false)
    @JsonIncludeProperties({"codigo", "nombre", "color"})
    private TipoJornada tipoJornada;

    @Column(name = "observacion", length = 500)
    private String observacion;
}
