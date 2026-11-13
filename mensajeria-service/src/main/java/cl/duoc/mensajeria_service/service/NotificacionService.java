package cl.duoc.mensajeria_service.service;

import cl.duoc.mensajeria_service.exception.ErrorNoRecuperableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lógica de negocio de las notificaciones: extrae el destinatario del evento
 * y delega el envío en {@link EmailService}. Los listeners no contienen esta lógica.
 */
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private static final Pattern EMAIL = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    private final EmailService emailService;

    public void notificar(String mensaje, String asunto) {
        String email = extraerEmail(mensaje);
        if (email == null) {
            throw new ErrorNoRecuperableException("El mensaje no contiene un email destinatario");
        }
        emailService.enviarCorreoHtml(email, asunto, mensaje);
    }

    String extraerEmail(String mensaje) {
        if (mensaje == null) return null;
        Matcher matcher = EMAIL.matcher(mensaje);
        return matcher.find() ? matcher.group() : null;
    }
}
