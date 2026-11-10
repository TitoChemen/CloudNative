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

/** Consumidor del dominio usuarios. La confirmación (ACK/NACK/DLQ) la gestiona {@link AckHandler}. */
@Component
@RequiredArgsConstructor
public class UsuariosListener {

    private static final String ASUNTO = "🎉 ¡Bienvenido a Pedidos360!";

    private final NotificacionService notificacionService;
    private final AckHandler ackHandler;

    @RabbitListener(queues = RabbitMQConfig.USUARIOS_QUEUE)
    public void recibir(String message, Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        ackHandler.procesar(RabbitMQConfig.USUARIOS_QUEUE, message, channel, deliveryTag,
                msg -> notificacionService.notificar(msg, ASUNTO));
    }
}
