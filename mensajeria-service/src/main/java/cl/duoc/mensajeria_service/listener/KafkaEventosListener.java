package cl.duoc.mensajeria_service.listener;

import cl.duoc.mensajeria_service.dto.PedidoEventoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventosListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventosListener.class);

    @KafkaListener(
        topics = "pedidos.eventos",
        groupId = "mensajeria-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void escucharEventosPedidos(@Payload PedidoEventoDTO evento) {
        log.info("[MENSAJERIA-SERVICE KAFKA] Evento de pedido procesado correctamente.");
        log.info("-> ID Evento: {}", evento.getEventoId());
        log.info("-> Tipo Evento: {}", evento.getTipoEvento());
        
        if (evento.getPayload() != null) {
            log.info("-> Pedido ID: {}, Cliente Email: {}, Total: ${}",
                    evento.getPayload().getPedidoId(),
                    evento.getPayload().getClienteEmail(),
                    evento.getPayload().getMontoTotal());
        }
    }
}