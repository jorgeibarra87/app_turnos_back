package com.turnos.enfermeria.service;

import com.turnos.enfermeria.model.dto.request.ActualizacionEstadoDTO;
import com.turnos.enfermeria.model.dto.response.NotificacionDTO;
import java.util.List;
import java.util.Optional;

public interface NotificacionService {
    NotificacionDTO create(NotificacionDTO notificacionDTO);
    NotificacionDTO update(NotificacionDTO detalleNotificacionDTO, Long id);
    Optional<NotificacionDTO> findById(Long idNotificacion);
    List<NotificacionDTO> findAll();
    void delete(Long idNotificacion);
    List<NotificacionDTO> getCorreosPredeterminadosActivos();
    List<NotificacionDTO> getCorreosSeleccionablesActivos();
    List<NotificacionDTO> getTodosCorreosActivos();
    List<NotificacionDTO> enviarNotificacionesAutomaticas(List<NotificacionDTO> notificaciones);
    List<NotificacionDTO> enviarNotificaciones(List<NotificacionDTO> notificaciones);
    NotificacionDTO agregarCorreoConfiguracion(NotificacionDTO notificacionDTO);
    List<NotificacionDTO> actualizarEstadosCorreos(List<ActualizacionEstadoDTO> actualizaciones);
    List<NotificacionDTO> getCorreosPorTipo(Boolean permanente);
    List<NotificacionDTO> getCorreosUnicos();
    NotificacionDTO createOrUpdate(NotificacionDTO notificacionDTO);
}
