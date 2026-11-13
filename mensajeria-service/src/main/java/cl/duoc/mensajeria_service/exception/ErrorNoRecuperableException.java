package cl.duoc.mensajeria_service.exception;

/**
 * Fallo permanente (mensaje mal formado, sin destinatario, etc.). Reintentar
 * no sirve: el consumidor lo envía directamente a la DLQ.
 */
public class ErrorNoRecuperableException extends RuntimeException {

    public ErrorNoRecuperableException(String message) {
        super(message);
    }

    public ErrorNoRecuperableException(String message, Throwable cause) {
        super(message, cause);
    }
}
