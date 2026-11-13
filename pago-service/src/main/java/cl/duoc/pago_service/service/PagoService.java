package cl.duoc.pago_service.service;

import cl.duoc.pago_service.config.RabbitMQConfig;
import cl.duoc.pago_service.dto.PagoRequest;
import cl.duoc.pago_service.dto.PedidoEventoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PagoService {

    private final RabbitTemplate rabbitTemplate;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> procesarPago(PagoRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        String emailCliente = request.getEmail() != null ? request.getEmail() : "cliente@gmail.com";

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

            // Publicar también el rechazo estructurado en Kafka (pedidos.eventos)
            publicarPedidoEventoKafka(request, "PAGO_RECHAZADO", emailCliente);

            respuesta.put("estado", "RECHAZADO");
            respuesta.put("mensaje", "Pago rechazado por la pasarela. Evento enviado a la DLQ y Kafka.");
            return respuesta;
        }

        // Transacción aprobada
        log.info("[PASARELA SIMULADA] Pago aprobado exitosamente para el pedido {}", request.getPedidoId());
        String mensajeExito = String.format("PAGO_APROBADO | Pedido: %s | Monto: $%.2f | Email: %s",
                request.getPedidoId(), request.getMonto(), emailCliente);

        // 1. Publicar en RabbitMQ
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAGOS_EXCHANGE,
                RabbitMQConfig.PAGOS_ROUTING_KEY,
                mensajeExito
        );

        // 2. Publicar PedidoEventoDTO serializado en Kafka ("pedidos.eventos")
        publicarPedidoEventoKafka(request, "PAGO_APROBADO", emailCliente);

        respuesta.put("estado", "APROBADO");
        respuesta.put("transaccionId", "TX-" + System.currentTimeMillis());
        respuesta.put("mensaje", "Pago procesado, publicado en RabbitMQ y enviado al tópico de Kafka.");
        return respuesta;
    }

    private void publicarPedidoEventoKafka(PagoRequest request, String tipoEvento, String emailCliente) {
        try {
            PedidoEventoDTO.PayloadPedido payload = new PedidoEventoDTO.PayloadPedido();
            String soloDigitos = request.getPedidoId() != null
                    ? request.getPedidoId().replaceAll("[^0-9]", "")
                    : "";
            payload.setPedidoId(soloDigitos.isEmpty() ? System.currentTimeMillis() : Long.parseLong(soloDigitos));
            payload.setClienteEmail(emailCliente);
            payload.setMontoTotal(request.getMonto() != null ? BigDecimal.valueOf(request.getMonto()) : BigDecimal.ZERO);
            payload.setMoneda("CLP");
            payload.setItems(request.getItems());

            PedidoEventoDTO evento = new PedidoEventoDTO(
                    UUID.randomUUID().toString(),
                    tipoEvento,
                    Instant.now().toString(),
                    "pago-service",
                    payload
            );

            kafkaProducerService.publicarEvento(
                    "pedidos.eventos",
                    String.valueOf(request.getPedidoId()),
                    objectMapper.writeValueAsString(evento)
            );
        } catch (Exception e) {
            log.error("No se pudo publicar PedidoEventoDTO en Kafka: {}", e.getMessage());
        }
    }
}