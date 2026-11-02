package cl.duoc.mensajeria_service.listener;

import cl.duoc.mensajeria_service.exception.ErrorNoRecuperableException;
import cl.duoc.mensajeria_service.exception.ErrorRecuperableException;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class AckHandlerTest {

    private static final long TAG = 7L;
    private final Channel channel = mock(Channel.class);
    // 3 intentos, 1 ms de espera: tests rápidos
    private final AckHandler handler = new AckHandler(3, 1, 1.0);

    @Test
    void mensajeExitosoHaceAck() throws Exception {
        handler.procesar("pagos.queue", "ok", channel, TAG, msg -> { });

        verify(channel).basicAck(TAG, false);
        verify(channel, never()).basicNack(TAG, false, false);
    }

    @Test
    void errorNoRecuperableVaDirectoALaDlqSinReintentar() throws Exception {
        AtomicInteger llamadas = new AtomicInteger();

        handler.procesar("pagos.queue", "malo", channel, TAG, msg -> {
            llamadas.incrementAndGet();
            throw new ErrorNoRecuperableException("sin email");
        });

        assertEquals(1, llamadas.get());
        verify(channel).basicNack(TAG, false, false);
        verify(channel, never()).basicAck(TAG, false);
    }

    @Test
    void errorRecuperableSeReintentaYLuegoHaceAck() throws Exception {
        AtomicInteger llamadas = new AtomicInteger();

        handler.procesar("pagos.queue", "msg", channel, TAG, msg -> {
            if (llamadas.incrementAndGet() < 2) {
                throw new ErrorRecuperableException("SMTP caído");
            }
        });

        assertEquals(2, llamadas.get());
        verify(channel).basicAck(TAG, false);
        verify(channel, never()).basicNack(TAG, false, false);
    }

    @Test
    void errorRecuperablePersistenteAgotaIntentosYVaALaDlq() throws Exception {
        AtomicInteger llamadas = new AtomicInteger();

        handler.procesar("pagos.queue", "msg", channel, TAG, msg -> {
            llamadas.incrementAndGet();
            throw new ErrorRecuperableException("SMTP caído");
        });

        assertEquals(3, llamadas.get());
        verify(channel).basicNack(TAG, false, false);
        verify(channel, never()).basicAck(TAG, false);
    }

    @Test
    void errorInesperadoVaALaDlq() throws Exception {
        handler.procesar("pagos.queue", "msg", channel, TAG, msg -> {
            throw new IllegalStateException("bug");
        });

        verify(channel).basicNack(TAG, false, false);
        verify(channel, never()).basicAck(TAG, false);
    }
}
