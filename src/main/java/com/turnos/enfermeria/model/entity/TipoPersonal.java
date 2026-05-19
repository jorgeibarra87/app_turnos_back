package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_personal", schema = "public")
public class TipoPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_personal", nullable = false)
    private Long idTipoPersonal;

    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "sigla", length = 10)
    private String sigla;

    @Column(name = "estado")
    private Boolean estado = true;
}
