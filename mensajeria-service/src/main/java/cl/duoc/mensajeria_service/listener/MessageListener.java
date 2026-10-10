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

    // Método auxiliar para extraer dinámicamente cualquier correo del mensaje
    private String extraerEmail(String mensaje) {
        if (mensaje == null) return null;
        Matcher matcher = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
                .matcher(mensaje);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    @RabbitListener(queues = RabbitMQConfig.USUARIOS_QUEUE)
    public void receiveUsuariosMessage(String message) {
        System.out.println("[RabbitMQ] Mensaje recibido en usuarios.queue: " + message);
        
        String emailDestino = extraerEmail(message);
        if (emailDestino != null) {
            emailService.enviarCorreoHtml(
                    emailDestino,
                    "🎉 ¡Bienvenido a Pedidos360!",
                    message
            );
        } else {
            System.err.println("[EMAIL WARN] No se encontró un correo válido en el mensaje de usuario.");
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PAGOS_QUEUE)
    public void receivePagosMessage(String message) {
        System.out.println("[RabbitMQ] Mensaje recibido en pagos.queue: " + message);
        
        String emailDestino = extraerEmail(message);
        if (emailDestino != null) {
            emailService.enviarCorreoHtml(
                    emailDestino,
                    "🛒 Confirmación de Compra - Pedidos360",
                    message
            );
        } else {
            System.err.println("[EMAIL WARN] No se encontró un correo válido en el mensaje de pago.");
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PRODUCTOS_QUEUE)
    public void receiveProductosMessage(String message) {
        System.out.println("[RabbitMQ] Mensaje recibido en productos.queue: " + message);
    }

    @RabbitListener(queues = RabbitMQConfig.PEDIDOS_QUEUE)
    public void receivePedidosMessage(String message) {
        System.out.println("[RabbitMQ] Mensaje recibido en pedidos.queue: " + message);
    }

    @RabbitListener(queues = RabbitMQConfig.USUARIOS_DLQ)
    public void receiveUsuariosDlqMessage(String message) {
        System.err.println("[DLQ ALERTA] Mensaje en usuarios.dlq: " + message);
    }

    @RabbitListener(queues = RabbitMQConfig.PRODUCTOS_DLQ)
    public void receiveProductosDlqMessage(String message) {
        System.err.println("[DLQ ALERTA] Mensaje en productos.dlq: " + message);
    }

    @RabbitListener(queues = RabbitMQConfig.PEDIDOS_DLQ)
    public void receivePedidosDlqMessage(String message) {
        System.err.println("[DLQ ALERTA] Mensaje en pedidos.dlq: " + message);
    }

    @RabbitListener(queues = RabbitMQConfig.PAGOS_DLQ)
    public void receivePagosDlqMessage(String message) {
        System.err.println("[DLQ ALERTA] Mensaje en pagos.dlq: " + message);
    }
}