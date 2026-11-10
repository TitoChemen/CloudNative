package cl.duoc.usuario_service.consumer;

import cl.duoc.usuario_service.dto.AuditoriaEventoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaConsumer.class);

    @KafkaListener(
        topics = "auditoria.actividad", 
        groupId = "auditoria-group", 
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void registrarAuditoria(@Payload AuditoriaEventoDTO evento) {
        log.info("[AUDITORIA KAFKA] Servicio: {} | Acción: {} | Usuario: {} | Timestamp: {}",
                evento.getServicio(), evento.getAccion(), evento.getUsuarioEmail(), evento.getTimestamp());
    }
}