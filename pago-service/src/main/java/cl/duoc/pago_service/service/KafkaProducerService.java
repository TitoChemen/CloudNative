package cl.duoc.pago_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEvento(String topico, String clave, Object evento) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topico, clave, evento);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[KAFKA PRODUCER SUCCESS] Evento publicado en Tópico: '{}' | Partición: {} | Offset: {}",
                        topico,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("[KAFKA PRODUCER ERROR] Error enviando evento a Tópico: '{}'. Causa: {}",
                        topico, ex.getMessage(), ex);
            }
        });
    }
}