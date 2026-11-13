package cl.duoc.mensajeria_service.consumer;

import cl.duoc.mensajeria_service.dto.PedidoEventoDTO;
import cl.duoc.mensajeria_service.exception.ErrorNoRecuperableException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.mail.username:offgdev@gmail.com}")
    private String remitente;

    @Value("${kafka.email.enabled:false}")
    private boolean enviarCorreoDesdeKafka;

    public PedidoConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(
            topics = "pedidos.eventos",
            groupId = "mensajeria-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void procesarNotificacionPedido(@Payload String mensajeJson) {
        PedidoEventoDTO evento;
        try {
            evento = objectMapper.readValue(mensajeJson, PedidoEventoDTO.class);
        } catch (Exception e) {
            throw new ErrorNoRecuperableException("JSON inválido en pedidos.eventos: " + e.getMessage(), e);
        }

        log.info("[KAFKA - pedidos.eventos] Tipo={} | EventoId={} | Origen={}",
                evento.getTipoEvento(), evento.getEventoId(), evento.getOrigen());

        if ("PAGO_RECHAZADO".equalsIgnoreCase(evento.getTipoEvento()) ||
            "PEDIDO_ERROR".equalsIgnoreCase(evento.getTipoEvento())) {
            log.warn("[KAFKA ALERT] Evento de fallo detectado en pedidos.eventos: {}", mensajeJson);
            return;
        }

        // Opcional: activable con KAFKA_EMAIL_ENABLED=true para no duplicar el correo de RabbitMQ
        if (!enviarCorreoDesdeKafka || evento.getPayload() == null || evento.getPayload().getClienteEmail() == null) {
            return;
        }

        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(remitente);
            email.setTo(evento.getPayload().getClienteEmail());
            email.setSubject("Confirmación de Pedido (Kafka) #" + evento.getPayload().getPedidoId());
            email.setText(String.format(
                    "¡Hola!\n\nTu pedido #%d por %s %s fue registrado en nuestro bus de eventos.\n\nEquipo Pedidos360.",
                    evento.getPayload().getPedidoId(),
                    evento.getPayload().getMontoTotal(),
                    evento.getPayload().getMoneda()
            ));
            mailSender.send(email);
        } catch (Exception e) {
            log.error("[KAFKA SMTP ERROR] Fallo al enviar correo: {}", e.getMessage());
            throw e;
        }
    }
}