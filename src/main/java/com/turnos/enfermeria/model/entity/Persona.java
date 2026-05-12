package com.turnos.enfermeria.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import jakarta.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "persona", schema = "public")
public class Persona {

    @Id
    @Column(name = "id_persona", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersona;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "documento")
    private String documento;

    @Column(name = "email")
    private String email;

    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles", joinColumns = @JoinColumn(name = "id_persona", referencedColumnName = "id_persona"),
            inverseJoinColumns = @JoinColumn(name = "rol_id", referencedColumnName = "id_rol")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Roles> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_titulos", joinColumns = @JoinColumn(name = "id_persona", referencedColumnName = "id_persona"),
            inverseJoinColumns = @JoinColumn(name = "id_titulo", referencedColumnName = "id_titulo")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<TitulosFormacionAcademica> titulosFormacionAcademica = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_equipo", joinColumns = @JoinColumn(name = "id_persona", referencedColumnName = "id_persona"),
            inverseJoinColumns = @JoinColumn(name = "id_equipo", referencedColumnName = "id_equipo")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Equipo> equipos = new HashSet<>();
}
