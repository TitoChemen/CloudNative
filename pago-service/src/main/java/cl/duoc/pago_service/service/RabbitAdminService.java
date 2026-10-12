package cl.duoc.pago_service.service;

import cl.duoc.pago_service.dto.QueueDTO;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {

    @Autowired
    private AmqpAdmin amqpAdmin;

    public String crearCola(QueueDTO dto) {
        Queue queue = new Queue(dto.getNombreCola(), dto.isDurable());
        String res = amqpAdmin.declareQueue(queue);
        return "Cola '" + res + "' creada exitosamente en RabbitMQ";
    }

    public boolean eliminarCola(String nombreCola) {
        return amqpAdmin.deleteQueue(nombreCola);
    }
}