package cl.duoc.pago_service.service;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private SendResult<String, Object> sendResult;

    @Mock
    private RecordMetadata recordMetadata;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    void debePublicarEventoEnTopicoYClaveCorrectos() {
        when(recordMetadata.partition()).thenReturn(0);
        when(recordMetadata.offset()).thenReturn(15L);
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(kafkaTemplate.send(eq("pedidos.eventos"), eq("ORD-295885"), any()))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        Map<String, Object> eventoSimulado = Map.of(
                "eventoId", "evt-100",
                "tipoEvento", "PAGO_APROBADO",
                "origen", "pago-service"
        );

        kafkaProducerService.publicarEvento("pedidos.eventos", "ORD-295885", eventoSimulado);

        verify(kafkaTemplate, times(1)).send("pedidos.eventos", "ORD-295885", eventoSimulado);
        verify(sendResult, times(2)).getRecordMetadata();
    }
}