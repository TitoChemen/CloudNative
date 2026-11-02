package cl.duoc.mensajeria_service.listener;

import cl.duoc.mensajeria_service.exception.ErrorRecuperableException;
import cl.duoc.mensajeria_service.service.NotificacionService;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PagosListenerTest {

    private static final long TAG = 1L;
    private static final String MSG = "PAGO_APROBADO | Pedido: 1 | Email: cliente@correo.cl";

    private final NotificacionService notificacion = mock(NotificacionService.class);
    private final Channel channel = mock(Channel.class);
    private final PagosListener listener = new PagosListener(notificacion, new AckHandler(3, 1, 1.0));

    @Test
    void pagoAprobadoNotificaYConfirma() throws Exception {
        listener.recibir(MSG, channel, TAG);

        verify(notificacion).notificar(eq(MSG), anyString());
        verify(channel).basicAck(TAG, false);
    }

    @Test
    void siFallaElEnvioReintentaYLuegoMandaADlq() throws Exception {
        doThrow(new ErrorRecuperableException("SMTP caído"))
                .when(notificacion).notificar(eq(MSG), anyString());

        listener.recibir(MSG, channel, TAG);

        verify(notificacion, times(3)).notificar(eq(MSG), anyString());
        verify(channel).basicNack(TAG, false, false);
    }
}
