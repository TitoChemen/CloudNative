package cl.duoc.mensajeria_service.consumer;

import cl.duoc.mensajeria_service.dto.PedidoEventoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PedidoConsumer {

    private static final Logger log = LoggerFactory.getLogger(PedidoConsumer.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:offgdev@gmail.com}")
    private String remitente;

    public PedidoConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(
        topics = "pedidos.eventos", 
        groupId = "mensajeria-group", 
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void procesarNotificacionPedido(@Payload PedidoEventoDTO evento) {
        log.info("[MENSAJERIA-SERVICE] Evento de pedido recibido desde Kafka. ID Evento: {}", evento.getEventoId());

        if (evento.getPayload() == null || evento.getPayload().getClienteEmail() == null) {
            log.warn("[MENSAJERIA-SERVICE] Evento omitido: No incluye correo de cliente válido.");
            return;
        }

        String destinatario = evento.getPayload().getClienteEmail();
        String asunto = "Confirmación de Compra - Pedido #" + evento.getPayload().getPedidoId();
        String mensajeTexto = String.format(
            "¡Hola!\n\nConfirmamos la recepción de tu pedido #%d.\n" +
            "Monto Total: %s %s\n\n" +
            "Gracias por comprar con nosotros.\n\n" +
            "Atentamente,\nEquipo de Plataforma Cloud Native.",
            evento.getPayload().getPedidoId(),
            evento.getPayload().getMontoTotal(),
            evento.getPayload().getMoneda()
        );

        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(remitente);
            email.setTo(destinatario);
            email.setSubject(asunto);
            email.setText(mensajeTexto);

            mailSender.send(email);
            log.info("[MENSAJERIA-SERVICE SUCCESS] Correo SMTP enviado exitosamente a: {}", destinatario);
        } catch (Exception e) {
            log.error("[MENSAJERIA-SERVICE ERROR] Fallo al enviar correo a {}: {}", destinatario, e.getMessage(), e);
            throw e; // Lanza la excepción para activar reintentos o manejo de fallos en Kafka
        }
    }
}