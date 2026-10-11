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
            case "PAGO_APROBADO" -> {
                String msg = String.format("PAGO_APROBADO | Pedido: ORD-SIM-%d | Monto: $120000.00 | Email: %s", idSim, email);
                rabbitTemplate.convertAndSend("pagos.exchange", "pagos.routingKey", msg);
                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "Evento enviado a [pagos.queue] -> Correo enviado");
            }
            case "PAGO_RECHAZADO" -> {
                String msg = String.format("PAGO_RECHAZADO | Pedido: ORD-SIM-DLQ-%d | Monto: $990000.00 | Email: %s", idSim, email);
                rabbitTemplate.convertAndSend("pagos.dlx", "pagos.dlq.routingKey", msg);
                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "Evento enviado a [pagos.dlq] (Dead Letter Queue)");
            }
            case "USUARIO_CREADO" -> {
                String msg = String.format("USUARIO_CREADO | ID: %d | Email: %s | Nombre: Usuario Demo Panel", idSim, email);
                rabbitTemplate.convertAndSend("usuarios.exchange", "usuarios.routingKey", msg);
                respuesta.put("estado", "OK");
                respuesta.put("mensaje", "Evento enviado a [usuarios.queue] -> Correo de Bienvenida enviado");
            }
            default -> {
                respuesta.put("estado", "ERROR");
                respuesta.put("mensaje", "Tipo de evento no reconocido: " + tipo);
            }
        }

        return respuesta;
    }
}