package cl.duoc.mensajeria_service.listener;

import cl.duoc.mensajeria_service.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @RabbitListener(queues = RabbitMQConfig.USUARIOS_QUEUE)
    public void receiveUsuariosMessage(String message) {
        System.out.println("[RabbitMQ] Mensaje recibido en usuarios.queue: " + message);
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
}