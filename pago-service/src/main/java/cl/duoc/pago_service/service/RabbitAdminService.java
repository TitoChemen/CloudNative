package cl.duoc.pago_service.service;

import cl.duoc.pago_service.dto.QueueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {

    private static final Logger log = LoggerFactory.getLogger(RabbitAdminService.class);

    @Autowired
    private AmqpAdmin amqpAdmin;

    public String crearCola(QueueDTO dto) {
        log.info("[RABBIT-ADMIN] Solicitando creación de cola dinámica: {}", dto.getNombreCola());
        
        Queue queue = dto.isDurable() 
                ? QueueBuilder.durable(dto.getNombreCola()).build()
                : QueueBuilder.nonDurable(dto.getNombreCola()).build();

        String res = amqpAdmin.declareQueue(queue);
        log.info("[RABBIT-ADMIN] Cola '{}' declarada exitosamente en el clúster RabbitMQ", res);
        return "Cola '" + res + "' creada exitosamente en RabbitMQ";
    }

    public boolean eliminarCola(String nombreCola) {
        log.info("[RABBIT-ADMIN] Solicitando eliminación de cola dinámica: {}", nombreCola);
        boolean eliminada = amqpAdmin.deleteQueue(nombreCola);
        log.info("[RABBIT-ADMIN] Resultado de eliminación para '{}': {}", nombreCola, eliminada);
        return eliminada;
    }
}