package cl.duoc.pago_service.service;

import cl.duoc.pago_service.config.RabbitMQConfig;
import cl.duoc.pago_service.dto.PagoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PagoService {

    private final RabbitTemplate rabbitTemplate;
    private final KafkaProducerService kafkaProducerService; // <-- 1. Inyectamos el productor de Kafka aquí

    public Map<String, Object> procesarPago(PagoRequest request) {
        Map<String, Object> respuesta = new HashMap<>();

        // 15% de probabilidad aleatoria de fallo O cuando la tarjeta venga como 'RECHAZADA'
        boolean esRechazado = "RECHAZADA".equalsIgnoreCase(request.getTarjeta()) || Math.random() < 0.15;

        if (esRechazado) {
            log.warn("[PASARELA SIMULADA] Pago rechazado para el pedido {}", request.getPedidoId());
            String mensajeFallo = String.format("PAGO_RECHAZADO | Pedido: %s | Monto: $%.2f", 
                    request.getPedidoId(), request.getMonto());
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAGOS_DLX, 
                    RabbitMQConfig.PAGOS_DLQ_ROUTING_KEY, 
                    mensajeFallo
            );

            respuesta.put("estado", "RECHAZADO");
            respuesta.put("mensaje", "Pago rechazado por la pasarela. Evento enviado a la DLQ.");
            return respuesta;
        }

        // Transacción aprobada sin importar el monto
        log.info("[PASARELA SIMULADA] Pago aprobado exitosamente para el pedido {}", request.getPedidoId());
        String mensajeExito = String.format("PAGO_APROBADO | Pedido: %s | Monto: $%.2f | Email: %s", 
                request.getPedidoId(), request.getMonto(), 
                request.getEmail() != null ? request.getEmail() : "cliente@gmail.com");

        // Publicar en RabbitMQ (lo que ya tenías)
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAGOS_EXCHANGE, 
                RabbitMQConfig.PAGOS_ROUTING_KEY, 
                mensajeExito
        );

        // 🚀 2. AQUÍ LLAMAMOS A KAFKA para cumplir con el tópico exigido: "pedidos.eventos"
        kafkaProducerService.publicarEvento(
                "pedidos.eventos",                // Nombre del tópico exigido por la rúbrica
                String.valueOf(request.getPedidoId()), // Clave (Key) del mensaje para particionamiento
                mensajeExito                      // El contenido del evento
        );

        respuesta.put("estado", "APROBADO");
        respuesta.put("transaccionId", "TX-" + System.currentTimeMillis());
        respuesta.put("mensaje", "Pago procesado, publicado en RabbitMQ y enviado al tópico de Kafka.");
        return respuesta;
    }
}