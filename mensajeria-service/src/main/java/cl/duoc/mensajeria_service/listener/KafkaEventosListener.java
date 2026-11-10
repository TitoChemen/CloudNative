package cl.duoc.mensajeria_service.listener;

import cl.duoc.mensajeria_service.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventosListener {

    private final NotificacionService notificacionService;

    @KafkaListener(topics = "pagos-topic", groupId = "mensajeria-group")
    public void consumirPagosKafka(String mensaje) {
        log.info("[KAFKA] Evento de pago recibido");
        // Tu NotificacionService hará la extracción por Regex y el envío de correo[cite: 115]
        notificacionService.notificar(mensaje, "🛒 Notificación de Pago (Kafka)");
    }

    @KafkaListener(topics = "pedidos-topic", groupId = "mensajeria-group")
    public void consumirPedidosKafka(String mensaje) {
        log.info("[KAFKA] Evento de pedido recibido");
        notificacionService.notificar(mensaje, "📦 Estado de tu Pedido (Kafka)");
    }

    @KafkaListener(topics = "usuarios-topic", groupId = "mensajeria-group")
    public void consumirUsuariosKafka(String mensaje) {
        log.info("[KAFKA] Evento de usuario recibido");
        notificacionService.notificar(mensaje, "🎉 ¡Bienvenido! (Kafka)");
    }
}