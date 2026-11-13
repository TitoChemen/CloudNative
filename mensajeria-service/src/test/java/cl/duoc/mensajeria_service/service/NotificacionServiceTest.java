package cl.duoc.mensajeria_service.service;

import cl.duoc.mensajeria_service.exception.ErrorNoRecuperableException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NotificacionServiceTest {

    private final EmailService emailService = mock(EmailService.class);
    private final NotificacionService service = new NotificacionService(emailService);

    @Test
    void enviaCorreoAlEmailDelMensaje() {
        String msg = "PAGO_APROBADO | Pedido: 1 | Email: cliente@correo.cl";

        service.notificar(msg, "Asunto");

        verify(emailService).enviarCorreoHtml("cliente@correo.cl", "Asunto", msg);
    }

    @Test
    void mensajeSinEmailEsErrorNoRecuperable() {
        assertThrows(ErrorNoRecuperableException.class,
                () -> service.notificar("PAGO_APROBADO | Pedido: 1", "Asunto"));
    }

    @Test
    void extraerEmailManejaNulos() {
        assertNull(service.extraerEmail(null));
        assertEquals("a@b.cl", service.extraerEmail("x a@b.cl y"));
    }
}
