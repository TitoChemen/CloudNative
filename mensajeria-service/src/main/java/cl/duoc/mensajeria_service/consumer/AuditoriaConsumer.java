package cl.duoc.mensajeria_service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditoriaConsumer {

    @KafkaListener(
            topics = "auditoria.actividad",
            groupId = "mensajeria-auditoria-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void registrarAuditoria(
            @Payload String eventoJson,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[KAFKA - auditoria.actividad] partition={} offset={} key={} payload={}",
                partition, offset, key, eventoJson);
    }
}