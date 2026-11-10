package cl.duoc.mensajeria_service.config;

import cl.duoc.mensajeria_service.exception.ErrorNoRecuperableException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (r, e) -> new TopicPartition(r.topic() + ".DLT", r.partition()));

        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxElapsedTime(4000L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        
        // ¡NUEVO!: Le indicamos a Kafka que NO reintente si es un error irrecuperable
        errorHandler.addNotRetryableExceptions(ErrorNoRecuperableException.class);
        
        return errorHandler;
    }
}