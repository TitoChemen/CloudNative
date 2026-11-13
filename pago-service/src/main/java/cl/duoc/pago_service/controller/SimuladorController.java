package cl.duoc.pago_service.controller;

import cl.duoc.pago_service.service.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/pagos/simular")
public class SimuladorController {

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Autowired(required = false)
    private KafkaProducerService kafkaProducerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/evento")
    public Map<String, String> simularEvento(@RequestBody Map<String, String> payload) {
        Map<String, String> respuesta = new HashMap<>();
        String tipo = payload.getOrDefault("tipo", "PAGO_APROBADO");
        String email = payload.getOrDefault("email", "offgdev@gmail.com");
        long idSim = System.currentTimeMillis() % 100000;

        if (rabbitTemplate == null) {
            respuesta.put("estado", "ERROR");
            respuesta.put("mensaje", "RabbitTemplate no está disponible en pago-service");
            return respuesta;
        }

        switch (tipo) {
            // COLAS PRINCIPALES (NORMALES)
            case "PAGO_APROBADO" -> {
                String pedidoId = "ORD-SIM-" + idSim;
                String msg = String.format("PAGO_APROBADO | Pedido: %s | Monto: $120000.00 | Email: %s", pedidoId, email);
                rabbitTemplate.convertAndSend("pagos.exchange", "pago.procesado", msg);

                String kafkaInfo = enviarAKafka("pedidos.eventos", pedidoId, Map.of(
                        "evento", "PAGO_APROBADO",
                        "pedidoId", pedidoId,
                        "monto", 120000.0,
                        "email", email,
                        "estado", "APROBADO"
                ));

                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "RabbitMQ [pagos.queue] + " + kafkaInfo);
            }
            case "USUARIO_CREADO" -> {
                String userId = String.valueOf(idSim);
                String msg = String.format("USUARIO_CREADO | ID: %s | Email: %s | Nombre: Usuario Demo Panel", userId, email);
                rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.routingKey", msg);

                String kafkaInfo = enviarAKafka("auditoria.actividad", userId, Map.of(
                        "accion", "USUARIO_CREADO",
                        "usuarioId", idSim,
                        "email", email
                ));

                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "RabbitMQ [usuarios.queue] + " + kafkaInfo);
            }
            case "PEDIDO_DESPACHADO" -> {
                String ordenId = "ORD-PED-" + idSim;
                String msg = String.format("PEDIDO_DESPACHADO | Orden: %s | Email: %s | Tracking: CL-TRK-%d", ordenId, email, idSim + 900);
                rabbitTemplate.convertAndSend("pedidos.exchange", "pedidos.routingKey", msg);

                String kafkaInfo = enviarAKafka("pedidos.eventos", ordenId, Map.of(
                        "evento", "PEDIDO_DESPACHADO",
                        "pedidoId", ordenId,
                        "email", email,
                        "estado", "DESPACHADO"
                ));

                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "RabbitMQ [pedidos.queue] + " + kafkaInfo);
            }
            case "STOCK_ACTUALIZADO" -> {
                String msg = String.format("STOCK_ACTUALIZADO | Producto: Teclado Mecánico RGB | Stock: 45 | Email: %s", email);
                rabbitTemplate.convertAndSend("productos.exchange", "productos.routingKey", msg);

                String kafkaInfo = enviarAKafka("auditoria.actividad", "PROD-" + idSim, Map.of(
                        "accion", "STOCK_ACTUALIZADO",
                        "producto", "Teclado Mecánico RGB",
                        "stock", 45,
                        "email", email
                ));

                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "RabbitMQ [productos.queue] + " + kafkaInfo);
            }

            // COLAS DE ERROR (DEAD LETTER QUEUES - DLQ)
            case "PAGO_RECHAZADO" -> {
                String pedidoId = "ORD-DLQ-" + idSim;
                String msg = String.format("PAGO_RECHAZADO | Pedido: %s | Motivo: Fondos Insuficientes | Email: %s", pedidoId, email);
                rabbitTemplate.convertAndSend("pagos.dlx", "pago.dead", msg);

                String kafkaInfo = enviarAKafka("pedidos.eventos", pedidoId, Map.of(
                        "evento", "PAGO_RECHAZADO",
                        "pedidoId", pedidoId,
                        "monto", 120000.0,
                        "email", email,
                        "estado", "RECHAZADO"
                ));

                respuesta.put("estado", "WARN");
                respuesta.put("mensaje", "RabbitMQ [pagos.dlq] + " + kafkaInfo);
            }
            case "USUARIO_ERROR" -> {
                String msg = String.format("USUARIO_ERROR | Fallo al registrar usuario | Email: %s | Motivo: RUT duplicado", email);
                rabbitTemplate.convertAndSend("usuarios.dlx", "usuarios.dlq.routingKey", msg);

                String kafkaInfo = enviarAKafka("auditoria.actividad", "ERR-" + idSim, Map.of(
                        "accion", "USUARIO_ERROR",
                        "motivo", "RUT duplicado",
                        "email", email
                ));

                respuesta.put("estado", "WARN");
                respuesta.put("mensaje", "RabbitMQ [usuarios.dlq] + " + kafkaInfo);
            }
            case "PEDIDO_ERROR" -> {
                String ordenId = "ORD-ERR-" + idSim;
                String msg = String.format("PEDIDO_ERROR | Orden: %s | Motivo: Dirección no válida | Email: %s", ordenId, email);
                rabbitTemplate.convertAndSend("pedidos.dlx", "pedidos.dlq.routingKey", msg);

                String kafkaInfo = enviarAKafka("pedidos.eventos", ordenId, Map.of(
                        "evento", "PEDIDO_ERROR",
                        "pedidoId", ordenId,
                        "motivo", "Dirección no válida",
                        "email", email,
                        "estado", "ERROR"
                ));

                respuesta.put("estado", "WARN");
                respuesta.put("mensaje", "RabbitMQ [pedidos.dlq] + " + kafkaInfo);
            }
            case "STOCK_AGOTADO" -> {
                String msg = String.format("STOCK_AGOTADO | Producto ID: 104 (Mouse Gamer) | Stock: 0 | Email: %s", email);
                rabbitTemplate.convertAndSend("productos.dlx", "productos.dlq.routingKey", msg);

                String kafkaInfo = enviarAKafka("auditoria.actividad", "PROD-104", Map.of(
                        "accion", "STOCK_AGOTADO",
                        "productoId", 104,
                        "stock", 0,
                        "email", email
                ));

                respuesta.put("estado", "WARN");
                respuesta.put("mensaje", "RabbitMQ [productos.dlq] + " + kafkaInfo);
            }
            default -> {
                respuesta.put("estado", "ERROR");
                respuesta.put("mensaje", "Tipo de evento desconocido: " + tipo);
            }
        }

        return respuesta;
    }

    private String enviarAKafka(String topico, String key, Map<String, Object> datos) {
        if (kafkaProducerService == null) {
            log.warn("KafkaProducerService no disponible en SimuladorController");
            return "Kafka [no disponible]";
        }
        try {
            Map<String, Object> payloadCompleto = new LinkedHashMap<>(datos);
            payloadCompleto.put("origen", "SIMULADOR");
            payloadCompleto.put("timestamp", Instant.now().toString());

            kafkaProducerService.publicarEvento(topico, key, objectMapper.writeValueAsString(payloadCompleto));
            return "Kafka [" + topico + " | key=" + key + "]";
        } catch (Exception e) {
            log.error("Error publicando desde simulador a Kafka: {}", e.getMessage());
            return "Kafka [error: " + e.getMessage() + "]";
        }
    }
}