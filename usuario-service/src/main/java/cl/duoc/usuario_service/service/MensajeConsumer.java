package cl.duoc.usuario_service.service;

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

    // Consumidor para la cola de tickets (por ejemplo, gestionada por usuarios)
    @RabbitListener(queues = "${rabbitmq.queues.tickets}")
    public void consumirTicket(@Payload String mensaje, 
                               Channel channel, 
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            LOGGER.info("=> Procesando ticket recibido en usuario-service: " + mensaje);
            
            if (mensaje.contains("error_forzado")) {
                throw new RuntimeException("Error simulado para probar la DLQ");
            }

            // ACK explícito si todo sale OK
            channel.basicAck(deliveryTag, false);
            LOGGER.info("=> Ticket procesado con éxito (ACK enviado).");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "=> Error procesando ticket: " + e.getMessage(), e);
            
            // NACK y directo a la DLQ
            channel.basicNack(deliveryTag, false, false);
            LOGGER.warning("=> Mensaje de ticket enviado a la Dead Letter Queue (DLQ).");
        }
    }
}