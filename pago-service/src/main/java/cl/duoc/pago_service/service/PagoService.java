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

    public Map<String, Object> procesarPago(PagoRequest request) {
        Map<String, Object> respuesta = new HashMap<>();

        // Simulación de regla de negocio / pasarela de pago
        if ("RECHAZADA".equalsIgnoreCase(request.getTarjeta()) || request.getMonto() > 500000) {
            log.warn("[PASARELA SIMULADA] Pago rechazado para el pedido {}", request.getPedidoId());
            
            String mensajeFallo = String.format("PAGO_RECHAZADO | Pedido: %s | Monto: $%.2f", 
                    request.getPedidoId(), request.getMonto());
            
            // Enviar directamente al Dead Letter Exchange para simular caída en DLQ[cite: 35]
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAGOS_DLX, 
                    RabbitMQConfig.PAGOS_DLQ_ROUTING_KEY, 
                    mensajeFallo
            );

            respuesta.put("estado", "RECHAZADO");
            respuesta.put("mensaje", "Pago rechazado por la pasarela. Evento enviado a DLQ.");
            return respuesta;
        }

        // Transacción aprobada
        log.info("[PASARELA SIMULADA] Pago aprobado exitosamente para el pedido {}", request.getPedidoId());
        String mensajeExito = String.format("PAGO_APROBADO | Pedido: %s | Monto: $%.2f", 
                request.getPedidoId(), request.getMonto());

        // Publicar evento al exchange principal de pagos[cite: 36]
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAGOS_EXCHANGE, 
                RabbitMQConfig.PAGOS_ROUTING_KEY, 
                mensajeExito
        );

        respuesta.put("estado", "APROBADO");
        respuesta.put("transaccionId", "TX-" + System.currentTimeMillis());
        respuesta.put("mensaje", "Pago procesado y evento publicado en RabbitMQ.");
        return respuesta;
    }
}