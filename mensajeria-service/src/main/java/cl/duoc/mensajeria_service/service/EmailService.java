package cl.duoc.mensajeria_service.service;

import cl.duoc.mensajeria_service.exception.ErrorNoRecuperableException;
import cl.duoc.mensajeria_service.exception.ErrorRecuperableException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${SPRING_MAIL_USERNAME:pedido360@kevinsosag.onmicrosoft.com}")
    private String remitente;

    /**
     * Envía el correo. Ya NO traga los errores: los propaga para que el consumidor
     * decida entre reintentar (recuperable) o mandar el mensaje a la DLQ (no recuperable).
     */
    public void enviarCorreoHtml(String destinatario, String asunto, String mensajeContenido) {
        if (mailSender == null) {
            throw new ErrorRecuperableException("JavaMailSender no está configurado (revisar SPRING_MAIL_*)");
        }

        MimeMessage message;
        try {
            message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject(asunto);

            String plantillahTML = """
                <div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;'>
                    <h2 style='color: #0056b3;'>Pedidos360 - Notificación de Evento</h2>
                    <p style='font-size: 16px; color: #333;'>Hola,</p>
                    <div style='background-color: #f8f9fa; padding: 15px; border-left: 4px solid #0056b3; margin: 15px 0;'>
                        <strong style='font-size: 15px;'>%s</strong>
                    </div>
                    <p style='color: #666; font-size: 13px;'>Este es un correo automático generado por el ecosistema de microservicios e integración orientada a eventos (RabbitMQ).</p>
                </div>
            """.formatted(mensajeContenido);

            helper.setText(plantillahTML, true);
        } catch (MessagingException e) {
            // Dirección o contenido inválido: reintentar no lo arregla.
            throw new ErrorNoRecuperableException("No se pudo construir el correo para " + destinatario + ": " + e.getMessage(), e);
        }

        try {
            mailSender.send(message);
            log.info("[EMAIL ENVIADO] Correo entregado exitosamente a {}", destinatario);
        } catch (MailException e) {
            // Fallo de transporte (SMTP caído, timeout, etc.): se puede reintentar.
            throw new ErrorRecuperableException("Error enviando correo a " + destinatario + ": " + e.getMessage(), e);
        }
    }
}
