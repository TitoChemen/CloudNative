package cl.duoc.pago_service.service;

import cl.duoc.pago_service.config.RabbitMQConfig;
import cl.duoc.pago_service.dto.PagoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PagoService {

    private final RabbitTemplate rabbitTemplate;
    
    // Inyectamos el servicio de Kafka que ya tienes creado en pago-service
    private final KafkaProducerService kafkaProducerService;

    public Map<String, Object> procesarPago(PagoRequest request) {
        Map<String, Object> respuesta = new HashMap<>();

        // 15% de probabilidad aleatoria de fallo O cuando la tarjeta venga como 'RECHAZADA'
        boolean esRechazado = "RECHAZADA".equalsIgnoreCase(request.getTarjeta()) || Math.random() < 0.15;

        if (esRechazado) {
            log.warn("[PASARELA SIMULADA] Pago rechazado para el pedido {}", request.getPedidoId());
            String mensajeFallo = String.format("PAGO_RECHAZADO | Pedido: %s | Monto: $%.2f", 
                    request.getPedidoId(), request.getMonto());
            
            // Envío a RabbitMQ (DLQ)
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAGOS_DLX, 
                    RabbitMQConfig.PAGOS_DLQ_ROUTING_KEY, 
                    mensajeFallo
            );

            // Opcional: Publicar evento de fallo en Kafka (Auditoría)
            kafkaProducerService.publicarEvento("auditoria.actividad", request.getPedidoId(), 
                Map.of("accion", "PAGO_RECHAZADO", "pedidoId", request.getPedidoId(), "timestamp", LocalDateTime.now().toString()));

            respuesta.put("estado", "RECHAZADO");
            respuesta.put("mensaje", "Pago rechazado por la pasarela. Evento enviado a la DLQ y Kafka.");
            return respuesta;
        }

        // Transacción aprobada sin importar el monto
        log.info("[PASARELA SIMULADA] Pago aprobado exitosamente para el pedido {}", request.getPedidoId());
        String mensajeExito = String.format("PAGO_APROBADO | Pedido: %s | Monto: $%.2f | Email: %s", 
                request.getPedidoId(), request.getMonto(), 
                request.getEmail() != null ? request.getEmail() : "cliente@gmail.com");

        // 1. Envío a RabbitMQ (Cola principal de pagos para correos)
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAGOS_EXCHANGE, 
                RabbitMQConfig.PAGOS_ROUTING_KEY, 
                mensajeExito
        );

        // 2. Envío a Kafka (Hacia el tópico "pedidos.eventos" configurado por tu compañero)
        Map<String, Object> eventoKafka = new HashMap<>();
        eventoKafka.put("eventoId", UUID.randomUUID().toString());
        eventoKafka.put("tipoEvento", "PAGO_EXITOSO");
        eventoKafka.put("pedidoId", request.getPedidoId());
        eventoKafka.put("monto", request.getMonto());
        eventoKafka.put("email", request.getEmail());
        eventoKafka.put("timestamp", LocalDateTime.now().toString());

        kafkaProducerService.publicarEvento("pedidos.eventos", request.getPedidoId(), eventoKafka);

        respuesta.put("estado", "APROBADO");
        respuesta.put("transaccionId", "TX-" + System.currentTimeMillis());
        respuesta.put("mensaje", "Pago procesado. Evento publicado en RabbitMQ y Kafka exitosamente.");
        return respuesta;
    }
}