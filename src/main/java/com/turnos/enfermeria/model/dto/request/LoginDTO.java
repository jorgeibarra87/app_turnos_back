package com.turnos.enfermeria.model.dto.request;

import lombok.Data;

@Data
public class LoginDTO {

    //private Long idPersona;
    private String documento;
    private String password;
}
