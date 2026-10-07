package cl.duoc.producto_service.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class MensajeConsumer {

    private static final Logger LOGGER = Logger.getLogger(MensajeConsumer.class.getName());

    // Consumidor para la cola de notificaciones
    @RabbitListener(queues = "${rabbitmq.queues.notificaciones}")
    public void consumirNotificacion(@Payload String mensaje, 
                                     Channel channel, 
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            LOGGER.info("=> Procesando notificación recibida: " + mensaje);
            
            // Simulamos una validación o lógica de negocio que podría fallar
            if (mensaje.contains("error_forzado")) {
                throw new RuntimeException("Error simulado para probar la DLQ");
            }

            // Si todo sale bien, confirmamos el mensaje manualmente (ACK explícito)
            channel.basicAck(deliveryTag, false);
            LOGGER.info("=> Notificación procesada y AOK (ACK enviado).");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "=> Error procesando notificación: " + e.getMessage(), e);
            
            // Si falla, rechazamos el mensaje y lo mandamos a la DLQ (requeue = false para que no vuelva a la misma cola)
            channel.basicNack(deliveryTag, false, false);
            LOGGER.warning("=> Mensaje enviado a la Dead Letter Queue (DLQ) por fallo.");
        }
    }
}