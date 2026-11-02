package cl.duoc.mensajeria_service.listener;

import cl.duoc.mensajeria_service.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor OPCIONAL de las colas de error. Por defecto está desactivado
 * (rabbitmq.dlq.listeners-enabled=false) para que los mensajes rechazados
 * permanezcan en las DLQ y se vean en el dashboard de RabbitMQ.
 * Al activarlo, solo registra el mensaje en el log y lo confirma (lo retira de la DLQ).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "rabbitmq.dlq.listeners-enabled", havingValue = "true")
public class DlqListener {

    @RabbitListener(queues = {
            RabbitMQConfig.USUARIOS_DLQ,
            RabbitMQConfig.PAGOS_DLQ,
            RabbitMQConfig.PEDIDOS_DLQ,
            RabbitMQConfig.PRODUCTOS_DLQ})
    public void registrar(String message, Channel channel,
                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                          @Header(AmqpHeaders.CONSUMER_QUEUE) String cola) throws IOException {
        log.error("[DLQ ALERTA] Mensaje capturado en {}: {}", cola, message);
        channel.basicAck(deliveryTag, false);
    }
}
