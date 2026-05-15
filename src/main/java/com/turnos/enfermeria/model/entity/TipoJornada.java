package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tipo_jornada", schema = "public")
public class TipoJornada {

    @Id
    @Column(name = "codigo", length = 10, nullable = false)
    private String codigo;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "hora_inicio", length = 5)
    private String horaInicio;

    @Column(name = "hora_fin", length = 5)
    private String horaFin;

    @Column(name = "es_descanso")
    private Boolean esDescanso = false;

    @Column(name = "es_trabajo")
    private Boolean esTrabajo = true;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "orden")
    private Integer orden = 0;

    @Column(name = "estado")
    private Boolean estado = true;
}
