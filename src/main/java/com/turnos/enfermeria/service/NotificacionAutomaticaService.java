package com.turnos.enfermeria.service;

public interface NotificacionAutomaticaService {
    void enviarNotificacionCambioCuadro(Long idCuadroTurno, String tipoOperacion, String detallesOperacion);
    void enviarNotificacionCambioTurno(Long idTurno, String tipoOperacion, String detallesOperacion);
    void enviarNotificacionCambioEquipo(Long idEquipo, String tipoOperacion, String detallesOperacion);
    void enviarNotificacionCambioPersonaEquipo(Long idPersona, Long idEquipo, String tipoOperacion, String detallesOperacion);
}
