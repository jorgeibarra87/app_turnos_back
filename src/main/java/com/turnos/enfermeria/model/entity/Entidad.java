package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "entidad", schema = "public")
public class Entidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entidad", nullable = false)
    private Long idEntidad;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "sigla", length = 20)
    private String sigla;

    @Column(name = "estado")
    private Boolean estado = true;
}
