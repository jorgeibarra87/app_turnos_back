package com.turnos.enfermeria.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "turnos", schema = "public")
public class Turnos {

    @Id
    @Column(name = "id_turno", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTurno;

    @ManyToOne
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona")
    @JsonIncludeProperties({"nombre", "documento"})
    private Persona usuario;

    @ManyToOne
    @JoinColumn(name = "id_cuadro_turno", referencedColumnName = "id_cuadro_turno")
    private CuadroTurno cuadroTurno;

    @Column(name = "fecha_inicio")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaFin;

    @Column(name = "total_horas")
    private Long totalHoras;

    @Column(name = "tipo_turno")
    private String tipoTurno;

    @Column(name = "estado_turno")
    private String estadoTurno;

    @Column(name = "jornada")
    private String jornada;

    @Column(name = "version")
    private String version;

    @Column(name = "comentarios")
    private String comentarios;

    @Column(name = "estado")
    private Boolean estado;

    public Long getIdPersona() {
        return usuario != null ? usuario.getIdPersona() : null;
    }
    public String getNombrePersona() {
        return usuario != null ? usuario.getNombreCompleto() : null;
    }
    public String getDocumento() {
        return usuario != null ? usuario.getDocumento() : null;
    }
}
