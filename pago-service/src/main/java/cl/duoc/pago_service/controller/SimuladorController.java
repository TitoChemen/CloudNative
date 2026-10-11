package cl.duoc.pago_service.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos/simular")
public class SimuladorController {

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

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
                String msg = String.format("PAGO_APROBADO | Pedido: ORD-SIM-%d | Monto: $120000.00 | Email: %s", idSim, email);
                rabbitTemplate.convertAndSend("pagos.exchange", "pagos.routingKey", msg);
                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "Publicado en [pagos.queue] -> Correo de Compra enviado");
            }
            case "USUARIO_CREADO" -> {
                String msg = String.format("USUARIO_CREADO | ID: %d | Email: %s | Nombre: Usuario Demo Panel", idSim, email);
                rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.routingKey", msg);
                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "Publicado en [usuarios.queue] -> Correo Bienvenida enviado");
            }
            case "PEDIDO_DESPACHADO" -> {
                String msg = String.format("PEDIDO_DESPACHADO | Orden: ORD-PED-%d | Email: %s | Tracking: CL-TRK-%d", idSim, email, idSim + 900);
                rabbitTemplate.convertAndSend("pedidos.exchange", "pedidos.routingKey", msg);
                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "Publicado en [pedidos.queue] -> Correo Despacho enviado");
            }
            case "STOCK_ACTUALIZADO" -> {
                String msg = String.format("STOCK_ACTUALIZADO | Producto: Teclado Mecánico RGB | Stock: 45 | Email: %s", email);
                rabbitTemplate.convertAndSend("productos.exchange", "productos.routingKey", msg);
                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "Publicado en [productos.queue] -> Evento de Inventario enviado");
            }

            // COLAS DE ERROR (DEAD LETTER QUEUES - DLQ)
            case "PAGO_RECHAZADO" -> {
                String msg = String.format("PAGO_RECHAZADO | Pedido: ORD-DLQ-%d | Motivo: Fondos Insuficientes | Email: %s", idSim, email);
                rabbitTemplate.convertAndSend("pagos.dlx", "pagos.dlq.routingKey", msg);
                respuesta.put("estado", "WARN");
                respuesta.put("mensaje", "Publicado en [pagos.dlq] (Dead Letter Queue)");
            }
            case "USUARIO_ERROR" -> {
                String msg = String.format("USUARIO_ERROR | Fallo al registrar usuario | Email: %s | Motivo: RUT duplicado", email);
                rabbitTemplate.convertAndSend("usuarios.dlx", "usuarios.dlq.routingKey", msg);
                respuesta.put("estado", "WARN");
                respuesta.put("mensaje", "Publicado en [usuarios.dlq] (Dead Letter Queue)");
            }
            case "PEDIDO_ERROR" -> {
                String msg = String.format("PEDIDO_ERROR | Orden: ORD-ERR-%d | Motivo: Dirección no válida | Email: %s", idSim, email);
                rabbitTemplate.convertAndSend("pedidos.dlx", "pedidos.dlq.routingKey", msg);
                respuesta.put("estado", "WARN");
                respuesta.put("mensaje", "Publicado en [pedidos.dlq] (Dead Letter Queue)");
            }
            case "STOCK_AGOTADO" -> {
                String msg = String.format("STOCK_AGOTADO | Producto ID: 104 (Mouse Gamer) | Stock: 0 | Email: %s", email);
                rabbitTemplate.convertAndSend("productos.dlx", "productos.dlq.routingKey", msg);
                respuesta.put("estado", "WARN");
                respuesta.put("mensaje", "Publicado en [productos.dlq] (Dead Letter Queue)");
            }
            default -> {
                respuesta.put("estado", "ERROR");
                respuesta.put("mensaje", "Tipo de evento desconocido: " + tipo);
            }
        }

        return respuesta;
    }
}