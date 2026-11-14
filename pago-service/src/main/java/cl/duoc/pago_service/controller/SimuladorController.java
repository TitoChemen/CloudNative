package cl.duoc.pago_service.controller;

import cl.duoc.pago_service.dto.PedidoEventoDTO;
import cl.duoc.pago_service.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/pagos/simular")
public class SimuladorController {

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Autowired(required = false)
    private KafkaProducerService kafkaProducerService;

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
            case "PAGO_APROBADO" -> {
                String pedidoId = "ORD-SIM-" + idSim;
                String msg = String.format("PAGO_APROBADO | Pedido: %s | Monto: $120000.00 | Email: %s", pedidoId, email);
                rabbitTemplate.convertAndSend("pagos.exchange", "pago.procesado", msg);

                PedidoEventoDTO dto = crearPedidoEventoDTO(idSim, "PAGO_APROBADO", email, BigDecimal.valueOf(120000.0));
                String kafkaInfo = enviarAKafka("pedidos.eventos", pedidoId, dto);

                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "RabbitMQ [pagos.queue] + " + kafkaInfo);
            }
            case "USUARIO_CREADO" -> {
                String userId = String.valueOf(idSim);
                String msg = String.format("USUARIO_CREADO | ID: %s | Email: %s | Nombre: Usuario Demo Panel", userId, email);
                rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.routingKey", msg);

                Map<String, Object> auditoria = crearAuditoriaPayload("USUARIO_CREADO", email);
                String kafkaInfo = enviarAKafka("auditoria.actividad", userId, auditoria);

                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "RabbitMQ [usuarios.queue] + " + kafkaInfo);
            }
            case "PEDIDO_DESPACHADO" -> {
                String ordenId = "ORD-PED-" + idSim;
                String msg = String.format("PEDIDO_DESPACHADO | Orden: %s | Email: %s | Tracking: CL-TRK-%d", ordenId, email, idSim + 900);
                rabbitTemplate.convertAndSend("pedidos.exchange", "pedidos.routingKey", msg);

                PedidoEventoDTO dto = crearPedidoEventoDTO(idSim, "PEDIDO_DESPACHADO", email, BigDecimal.ZERO);
                String kafkaInfo = enviarAKafka("pedidos.eventos", ordenId, dto);

                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "RabbitMQ [pedidos.queue] + " + kafkaInfo);
            }
            case "STOCK_ACTUALIZADO" -> {
                String msg = String.format("STOCK_ACTUALIZADO | Producto: Teclado Mecánico RGB | Stock: 45 | Email: %s", email);
                rabbitTemplate.convertAndSend("productos.exchange", "productos.routingKey", msg);

                Map<String, Object> auditoria = crearAuditoriaPayload("STOCK_ACTUALIZADO", email);
                String kafkaInfo = enviarAKafka("auditoria.actividad", "PROD-" + idSim, auditoria);

                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "RabbitMQ [productos.queue] + " + kafkaInfo);
            }
            case "PAGO_RECHAZADO" -> {
                String pedidoId = "ORD-DLQ-" + idSim;
                String msg = String.format("PAGO_RECHAZADO | Pedido: %s | Motivo: Fondos Insuficientes | Email: %s", pedidoId, email);
                rabbitTemplate.convertAndSend("pagos.dlx", "pago.dead", msg);

                PedidoEventoDTO dto = crearPedidoEventoDTO(idSim, "PAGO_RECHAZADO", email, BigDecimal.valueOf(120000.0));
                String kafkaInfo = enviarAKafka("pedidos.eventos", pedidoId, dto);

                respuesta.put("estado", "WARN");
                respuesta.put("mensaje", "RabbitMQ [pagos.dlq] + " + kafkaInfo);
            }
            case "USUARIO_ERROR" -> {
                String msg = String.format("USUARIO_ERROR | Fallo al registrar usuario | Email: %s | Motivo: RUT duplicado", email);
                rabbitTemplate.convertAndSend("usuarios.dlx", "usuarios.dlq.routingKey", msg);

                Map<String, Object> auditoria = crearAuditoriaPayload("USUARIO_ERROR", email);
                String kafkaInfo = enviarAKafka("auditoria.actividad", "ERR-" + idSim, auditoria);

                respuesta.put("estado", "WARN");
                respuesta.put("mensaje", "RabbitMQ [usuarios.dlq] + " + kafkaInfo);
            }
            case "PEDIDO_ERROR" -> {
                String ordenId = "ORD-ERR-" + idSim;
                String msg = String.format("PEDIDO_ERROR | Orden: %s | Motivo: Dirección no válida | Email: %s", ordenId, email);
                rabbitTemplate.convertAndSend("pedidos.dlx", "pedidos.dlq.routingKey", msg);

                PedidoEventoDTO dto = crearPedidoEventoDTO(idSim, "PEDIDO_ERROR", email, BigDecimal.ZERO);
                String kafkaInfo = enviarAKafka("pedidos.eventos", ordenId, dto);

                respuesta.put("estado", "WARN");
                respuesta.put("mensaje", "RabbitMQ [pedidos.dlq] + " + kafkaInfo);
            }
            case "STOCK_AGOTADO" -> {
                String msg = String.format("STOCK_AGOTADO | Producto ID: 104 (Mouse Gamer) | Stock: 0 | Email: %s", email);
                rabbitTemplate.convertAndSend("productos.dlx", "productos.dlq.routingKey", msg);

                Map<String, Object> auditoria = crearAuditoriaPayload("STOCK_AGOTADO", email);
                String kafkaInfo = enviarAKafka("auditoria.actividad", "PROD-104", auditoria);

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

    private PedidoEventoDTO crearPedidoEventoDTO(long pedidoId, String tipoEvento, String email, BigDecimal monto) {
        PedidoEventoDTO.PayloadPedido payloadPedido = new PedidoEventoDTO.PayloadPedido();
        payloadPedido.setPedidoId(pedidoId);
        payloadPedido.setClienteEmail(email);
        payloadPedido.setMontoTotal(monto);
        payloadPedido.setMoneda("CLP");

        return new PedidoEventoDTO(
                UUID.randomUUID().toString(),
                tipoEvento,
                Instant.now().toString(),
                "SIMULADOR",
                payloadPedido
        );
    }

    private Map<String, Object> crearAuditoriaPayload(String accion, String email) {
        Map<String, Object> auditoria = new LinkedHashMap<>();
        auditoria.put("auditId", UUID.randomUUID().toString());
        auditoria.put("servicio", "SIMULADOR");
        auditoria.put("accion", accion);
        auditoria.put("usuarioEmail", email);
        auditoria.put("timestamp", Instant.now().toString());
        return auditoria;
    }

    private String enviarAKafka(String topico, String key, Object evento) {
        if (kafkaProducerService == null) {
            log.warn("KafkaProducerService no disponible en SimuladorController");
            return "Kafka [no disponible]";
        }
        try {
            kafkaProducerService.publicarEvento(topico, key, evento);
            return "Kafka [" + topico + " | key=" + key + "]";
        } catch (Exception e) {
            log.error("Error publicando desde simulador a Kafka: {}", e.getMessage());
            return "Kafka [error: " + e.getMessage() + "]";
        }
    }
}