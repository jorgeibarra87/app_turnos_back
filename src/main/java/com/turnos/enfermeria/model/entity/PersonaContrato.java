package com.turnos.enfermeria.model.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "usuario_contrato", schema = "public")
public class PersonaContrato {

    @Id
    @Column(name = "id_usuario_contrato", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersonaContrato;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona")
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "id_contrato", referencedColumnName = "id_contrato")
    private Contrato contrato;

    @ManyToOne
    @JoinColumn(name = "id_rol", referencedColumnName = "id_rol")
    private Roles roles;

    @Column(name = "estado")
    private Boolean estado;

    public Long getIdContrato() {
        return contrato != null ? contrato.getIdContrato() : null;
    }
    public Long getIdPersona() { return persona != null ? persona.getIdPersona() : null;}
    public Long getIdRol() { return roles != null ? roles.getIdRol() : null;}
}
