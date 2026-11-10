package cl.duoc.mensajeria_service.exception;

/**
 * Fallo transitorio (p. ej. SMTP caído). El consumidor reintenta el mensaje
 * y, si se agotan los intentos, lo envía a la DLQ.
 */
public class ErrorRecuperableException extends RuntimeException {

    public ErrorRecuperableException(String message) {
        super(message);
    }

    public ErrorRecuperableException(String message, Throwable cause) {
        super(message, cause);
    }
}
