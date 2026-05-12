package com.turnos.enfermeria.service;

import jakarta.mail.MessagingException;
import java.util.List;
import java.util.Map;

public interface EmailService {
    void enviarCorreoHTML(String destinatario, String asunto, String contenidoHTML) throws MessagingException;
    Map<String, Boolean> enviarCorreosMasivos(List<String> destinatarios, String asunto, String contenidoHTML);
    boolean isConfiguracionValida();
    boolean enviarCorreoPrueba(String destinatario);
    void enviarCorreoTexto(String destinatario, String asunto, String contenido) throws MessagingException;
}
