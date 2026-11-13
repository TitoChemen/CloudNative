package cl.duoc.mensajeria_service.listener;

import cl.duoc.mensajeria_service.config.RabbitMQConfig;
import cl.duoc.mensajeria_service.service.NotificacionService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Consumidor del dominio productos. La confirmación (ACK/NACK/DLQ) la gestiona {@link AckHandler}. */
@Component
@RequiredArgsConstructor
public class ProductosListener {

    private static final String ASUNTO = "🏷️ Actualización de Inventario - Pedidos360";

    private final NotificacionService notificacionService;
    private final AckHandler ackHandler;

    @RabbitListener(queues = RabbitMQConfig.PRODUCTOS_QUEUE)
    public void recibir(String message, Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        ackHandler.procesar(RabbitMQConfig.PRODUCTOS_QUEUE, message, channel, deliveryTag,
                msg -> notificacionService.notificar(msg, ASUNTO));
    }
}
