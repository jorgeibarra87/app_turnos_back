package com.turnos.enfermeria.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonaContratoTotalDTO {

    private String documento;
    private String nombre;
    private String telefono;
    private String email;
    private String profesion;
    private String contrato;
    private String rol;
}
