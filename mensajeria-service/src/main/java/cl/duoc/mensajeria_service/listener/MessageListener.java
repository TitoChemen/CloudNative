package cl.duoc.mensajeria_service.listener;

import cl.duoc.mensajeria_service.config.RabbitMQConfig;
import cl.duoc.mensajeria_service.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MessageListener {

    @Autowired
    private EmailService emailService;

    private String extraerEmail(String mensaje) {
        if (mensaje == null) return null;
        Matcher matcher = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
                .matcher(mensaje);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    // --- COLAS PRINCIPALES ---
    @RabbitListener(queues = RabbitMQConfig.USUARIOS_QUEUE)
    public void receiveUsuariosMessage(String message) {
        System.out.println("[RabbitMQ] Mensaje en usuarios.queue: " + message);
        String email = extraerEmail(message);
        if (email != null) {
            emailService.enviarCorreoHtml(email, "🎉 ¡Bienvenido a Pedidos360!", message);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PAGOS_QUEUE)
    public void receivePagosMessage(String message) {
        System.out.println("[RabbitMQ] Mensaje en pagos.queue: " + message);
        String email = extraerEmail(message);
        if (email != null) {
            emailService.enviarCorreoHtml(email, "🛒 Confirmación de Compra - Pedidos360", message);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PEDIDOS_QUEUE)
    public void receivePedidosMessage(String message) {
        System.out.println("[RabbitMQ] Mensaje en pedidos.queue: " + message);
        String email = extraerEmail(message);
        if (email != null) {
            emailService.enviarCorreoHtml(email, "📦 Estado de tu Pedido - Pedidos360", message);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PRODUCTOS_QUEUE)
    public void receiveProductosMessage(String message) {
        System.out.println("[RabbitMQ] Mensaje en productos.queue: " + message);
        String email = extraerEmail(message);
        if (email != null) {
            emailService.enviarCorreoHtml(email, "🏷️ Actualización de Inventario - Pedidos360", message);
        }
    }

    // --- DEAD LETTER QUEUES (DLQ) ---
    @RabbitListener(queues = RabbitMQConfig.USUARIOS_DLQ)
    public void receiveUsuariosDlqMessage(String message) {
        System.err.println("[DLQ ALERTA] Fallo capturado en usuarios.dlq: " + message);
        String email = extraerEmail(message);
        if (email != null) {
            emailService.enviarCorreoHtml(email, "⚠️ Alerta DLQ: Error en Registro de Usuario", message);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PAGOS_DLQ)
    public void receivePagosDlqMessage(String message) {
        System.err.println("[DLQ ALERTA] Fallo capturado en pagos.dlq: " + message);
        String email = extraerEmail(message);
        if (email != null) {
            emailService.enviarCorreoHtml(email, "⚠️ Alerta DLQ: Pago Rechazado / Fallido", message);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PEDIDOS_DLQ)
    public void receivePedidosDlqMessage(String message) {
        System.err.println("[DLQ ALERTA] Fallo capturado en pedidos.dlq: " + message);
        String email = extraerEmail(message);
        if (email != null) {
            emailService.enviarCorreoHtml(email, "⚠️ Alerta DLQ: Error en Procesamiento de Pedido", message);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PRODUCTOS_DLQ)
    public void receiveProductosDlqMessage(String message) {
        System.err.println("[DLQ ALERTA] Fallo capturado en productos.dlq: " + message);
        String email = extraerEmail(message);
        if (email != null) {
            emailService.enviarCorreoHtml(email, "🚨 Alerta DLQ Urgente: Stock Crítico / Agotado", message);
        }
    }
}