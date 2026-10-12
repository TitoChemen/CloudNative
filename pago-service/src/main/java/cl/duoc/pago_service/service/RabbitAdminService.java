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
        System.out.println("[RABBIT-ADMIN] Solicitando creación de cola dinámica: " + dto.getNombreCola());
        Queue queue = new Queue(dto.getNombreCola(), dto.isDurable());
        String res = amqpAdmin.declareQueue(queue);
        System.out.println("[RABBIT-ADMIN] Cola '" + res + "' declarada exitosamente en el clúster RabbitMQ");
        return "Cola '" + res + "' creada exitosamente en RabbitMQ";
    }

    public boolean eliminarCola(String nombreCola) {
        System.out.println("[RABBIT-ADMIN] Solicitando eliminación de cola dinámica: " + nombreCola);
        boolean eliminada = amqpAdmin.deleteQueue(nombreCola);
        System.out.println("[RABBIT-ADMIN] Resultado de eliminación para '" + nombreCola + "': " + eliminada);
        return eliminada;
    }
}